# NevvaVisuals — клиентский мод для Minecraft 1.21.4 (Fabric)

Клиентский визуальный мод с аккуратным HUD, Target HUD, анимированным меню
и настройками мира/рендера на стороне клиента.

## Возможности

**HUD**

| Модуль | Что делает | По умолчанию |
| --- | --- | --- |
| HUD | FPS, координаты, направление, скорость, пинг, TPS, память + список включённых модулей | вкл |
| Target HUD | Показывает последнего игрока, которого вы ударили: имя, HP, дистанция. Держится 3 секунды после последнего удара | вкл |
| Keystrokes | Анимированные клавиши W A S D + LMB/RMB | вкл |
| Watermark | Логотип NevvaVisuals с плавным градиентом | вкл |

**Render**

| Модуль | Что делает |
| --- | --- |
| Zoom | Приближение по удержанию `C`, сила настраивается |
| Fullbright | Поднимает яркость мира |
| Aspect Ratio | Растягивает картинку под произвольное соотношение сторон |
| Custom Fog | Своя дистанция начала и конца тумана |
| No View Bob | Убирает покачивание камеры |
| No Damage Tilt | Убирает наклон камеры от урона |
| No Nausea | Убирает искажение экрана |
| No Darkness | Отключает эффект темноты |
| No Lightning Flash | Убирает вспышку от молнии |
| Cinematic | Прячет весь HUD для скриншотов |

**World**

| Модуль | Что делает |
| --- | --- |
| Custom World | Своё время суток (слайдер 0–23999) и ясная погода, только на клиенте |
| Particles | Режим частиц: `ALL` / `DECREASED` / `MINIMAL` |

**Player**

| Модуль | Что делает |
| --- | --- |
| Auto Sprint | Автоспринт, пока держите движение вперёд |

## Управление

| Клавиша | Действие |
| --- | --- |
| `Right Shift` | Открыть / закрыть меню |
| `C` | Зум (удерживать) |
| `H` | Включить / выключить HUD |

Клавиши меняются в «Настройки → Управление» в категории **NevvaVisuals**.

## Меню

Слева — вкладки категорий (`All`, `HUD`, `Render`, `World`, `Player`),
в центре — список модулей с анимированными переключателями, справа — настройки
выбранного модуля: тумблеры, слайдеры (тянутся мышью) и выбор из списка.
Меню открывается с лёгкой анимацией, строки подсвечиваются плавно, при
переключении модуля справа снизу выезжает уведомление.

## Установка

1. **Fabric Loader 0.16.9+** для Minecraft **1.21.4**.
2. **Fabric API** для 1.21.4 в папку `mods`.
3. Собранный `nevvavisuals-1.0.0.jar` в папку `mods`.
4. Запустить профиль Fabric 1.21.4.

Мод клиентский: на сервер его ставить не нужно. Aspect Ratio, Custom Fog
и Custom World меняют только то, что видите вы, сервер об этом не знает.

## Сборка jar на GitHub

Workflow уже лежит в репозитории — ничего настраивать не нужно.

1. Запушьте изменения в ветку (или нажмите `Run workflow` на вкладке **Actions**).
2. Откройте **Actions → Build mod jar**.
3. После зелёной галочки откройте запуск → **Artifacts** → `nevvavisuals-jar`.
4. Внутри архива — `nevvavisuals-1.0.0.jar`, это и есть готовый мод.

## Локальная сборка (нужен JDK 21)

```bash
chmod +x gradlew     # один раз, Linux / macOS
./gradlew build      # Windows: gradlew.bat build
```

Готовый файл: `build/libs/nevvavisuals-1.0.0.jar`.
Запуск игры для проверки: `./gradlew runClient`.

## Конфиг

Файл: `.minecraft/config/nevvavisuals.json`

```json
{
  "modules": { "HUD": true, "Target HUD": true, "Zoom": true },
  "bools": { "clear_weather": true },
  "numbers": { "zoom_fov": 30.0, "aspect_ratio": 1.7778, "world_time": 6000.0,
               "fog_start": 40.0, "fog_end": 300.0 },
  "choices": { "particles_mode": "DECREASED" },
  "hudBackground": true,
  "notifications": true
}
```

Всё это меняется и из меню — файл перезаписывается автоматически при
переключении модулей и настройках, а также при выходе из игры.

## Технические детали

- Minecraft **1.21.4**, Yarn **1.21.4+build.8**, Fabric Loader **0.16.9**,
  Fabric API **0.119.4+1.21.4**, Fabric Loom **1.10.5**, Java **21**.
- Три миксина:
  - `GameRendererMixin` — `getBasicProjectionMatrix` для Aspect Ratio;
  - `BackgroundRendererMixin` — `applyFog` для Custom Fog;
  - `WorldMixin` — `getTimeOfDay` / `getSkyAngleRadians` / `getRainGradient` /
    `getThunderGradient` для Custom World. Меняется только клиентский мир.
- `nevvavisuals.accesswidener` открывает `BackgroundRenderer$FogType`
  (в 1.21.4 он package-private), иначе миксин тумана не собрать.

## Структура

```
src/main/java/dev/nevva/visuals/
  NevvaVisuals.java        точка входа, тик и рендер HUD
  Keybinds.java            горячие клавиши
  anim/Animation.java      плавные анимации (независимые от FPS)
  config/                  конфиг (Gson, config/nevvavisuals.json)
  gui/NevvaScreen.java     меню: вкладки, модули, настройки
  hud/                     HUD, Target HUD, Keystrokes, Watermark, уведомления
  mixin/                   три миксина
  module/                  Module, OptionModule, ModuleManager, Category
  module/modules/          все модули
  module/setting/          тумблеры, слайдеры, списки
  render/RenderUtils.java  скруглённые панели, градиенты, текст
```

## Добавить свой модуль

1. Класс в `dev.nevva.visuals.module.modules`, наследник `Module`
   (`super("Name", "Description", Category.RENDER)`).
2. Настройки: `add(new BoolSetting("key", "Label", true))`,
   `add(new NumberSetting("key", "Label", 50.0, 0.0, 100.0, 1.0))`,
   `add(new ChoiceSetting("key", "Label", "A", "A", "B"))`. Ключи должны быть
   уникальны — по ним значения пишутся в конфиг.
3. Если модуль просто подменяет опцию игры — наследуйтесь от `OptionModule<T>`
   и реализуйте `getOption()` и `getOverrideValue()`: значение игрока
   восстановится при выключении.
4. Зарегистрируйте модуль в `ModuleManager`.
5. Если нужна отрисовка — добавьте класс в `hud/` по примеру `TargetHud`
   и вызовите его из `NevvaVisuals.onClientTick` / `renderHud`.

## Замечание

Aspect Ratio, Custom Fog, Zoom и Fullbright на некоторых серверах могут
считаться преимуществом. Используйте на свой страх и риск и соблюдайте
правила сервера.
