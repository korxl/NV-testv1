# NevvaVisuals 1.2 — визуальный клиент для Minecraft 1.21.4 (Fabric)

Чисто визуальный клиент: HUD, TargetHUD, кейстроки, watermark, кейстроки-инфо, темы,
темы, анимации и меню в стиле популярных клиентов. Читерских функций нет — только
оформление и косметика.

## Меню

`Right Shift` — открыть меню. `Left Alt` — редактор HUD.

- Слева: логотип, поиск по модулям (печатай сразу, Backspace — стереть) и категории
  `All / HUD / Render / World / Client / Misc` с иконками.
- В центре: карточки модулей с анимированными тумблерами и подсветкой при наведении.
- Справа: описание модуля и его настройки — тумблеры, слайдеры с круглым ползунком,
  списки `< значение >` и hue-слайдеры цвета с превью.
- Клик по карточке сопровождается ripple-эффектом; меню открывается с плавным
  масштабированием; углы панелей сглажены с антиалиасингом.
- **Меню автоматически подгоняется под любой экран** (в том числе 14 Pro Max):
  панель рисуется в фиксированной сетке 600×360 и масштабируется так, чтобы
  полностью влезать по ширине и высоте, а клики пересчитываются в её координаты.

## Темы и шрифт

Категория `Client` → модуль `Interface`:

| Настройка | Значения |
| --- | --- |
| Theme | `Nursultan` (циан), `Sadness` (фиолетовый), `Rockstar` (золото), `Delta` (синий), `Wex` (неон-зелёный), `Custom` |
| Custom accent | hue-слайдер акцента для темы `Custom` |
| Font | `Default` (как в системе), `Caps` (капс), `Soft` (без тени) |
| Notifications | включить/выключить карточки уведомлений |

Minecraft несёт только один шрифт, поэтому «Font» меняет стиль отрисовки текста
(регистр/тень), а не подменяет файл шрифта — так интерфейс остаётся читаемым на
любом языке.

## HUD и редактор

`Left Alt` открывает редактор HUD:

- перетаскивание элементов мышью, позиция сохраняется в конфиг;
- колесо мыши над элементом меняет размер;
- правый клик включает/выключает элемент.

Доступные элементы: FPS, Ping, Координаты, Компас (направление + угол), Speed,
CPS, Armor, Effects, TPS, Memory, Time. Каждый можно двигать и масштабировать
независимо, в модуле `HUD` есть общий фон подложки и тень текста.

`ArrayList` (модуль, по умолчанию выключен) — список включённых модулей с
градиентом, полоской-индикатором, выбором стороны и масштабом.

## Модули

**HUD**

| Модуль | Настройки |
| --- | --- |
| HUD | фон подложки, тень текста (позиции — в редакторе HUD) |
| Target HUD | масштаб, дистанция, полоска здоровья |
| Keystrokes | масштаб, кнопки мыши, пробел |
| Watermark | масштаб (ник + версия) |
| ArrayList | сторона, полоска, градиент, масштаб |

**Render**

| Модуль | Настройки |
| --- | --- |
| Zoom | FOV при удержании `C` |
| Fullbright | сила (гамма 1–20) |
| Aspect Ratio | произвольное соотношение сторон |
| No View Bob | только от первого лица |
| No Damage Tilt | сила 0–100 % |
| No Nausea | сила 0–100 % |
| No Darkness | сила 0–100 % |

**World**

| Модуль | Настройки |
| --- | --- |
| Custom World | своё время суток, цвет неба (hue), ясная погода |
| Custom Fog | начало/конец тумана, цвет тумана, включение цвета |
| Particles | режим `ALL / DECREASED / MINIMAL` |

**Client / Misc**

| Модуль | Настройки |
| --- | --- |
| Interface | тема, акцент, шрифт, уведомления |
| Auto Sprint | задержка в тиках |

Клавиши: `Right Shift` — меню, `Left Alt` — редактор HUD, `C` — зум, `H` — HUD.

## Установка

1. **Fabric Loader 0.16.9+** для Minecraft **1.21.4**.
2. **Fabric API** для 1.21.4 в папку `mods`.
3. `nevvavisuals-1.0.0.jar` в папку `mods`.

## Скачать jar

Готовый jar всегда лежит в последнем релизе:
https://github.com/korxl/NV-testv1/releases/latest/download/nevvavisuals-1.0.0.jar

После каждого пуша GitHub Actions собирает мод и обновляет релиз `latest`
(а также кладёт jar в Artifacts запуска).

Локальная сборка (нужен JDK 21):

```bash
chmod +x gradlew
./gradlew build      # Windows: gradlew.bat build
```

## Конфиг

`.minecraft/config/nevvavisuals.json`

```json
{
  "modules": { "HUD": true, "ArrayList": false, "Interface": true },
  "bools": { "hud_background": true, "notifications": true, "hud_fps": true },
  "numbers": { "zoom_fov": 30.0, "world_time": 6000.0, "accent_hue": 190.0,
               "hud_fps_x": 0.006, "hud_fps_y": 0.03, "hud_fps_scale": 1.0 },
  "choices": { "theme": "Nursultan", "font": "Default", "particles_mode": "DECREASED" },
  "notifications": true
}
```

Позиции HUD-элементов хранятся как доли экрана (`hud_<id>_x`, `hud_<id>_y`),
размер — `hud_<id>_scale`.

## Технические детали

Minecraft **1.21.4**, Yarn **1.21.4+build.8**, Fabric Loader **0.16.9**,
Fabric API **0.119.4+1.21.4**, Fabric Loom **1.10.5**, Gradle **8.12**, Java **21**.

Миксины: `GameRenderer` (аспект), `BackgroundRenderer` (туман: дистанция и цвет),
`World` (время/погода), `ClientWorld` (цвет неба). Аксесс-виденер открывает
`BackgroundRenderer$FogType` и `MinecraftClient.itemUseCooldown`.

```
src/main/java/dev/nevva/visuals/
  NevvaVisuals.java       точка входа, тик и рендер HUD
  Keybinds.java           горячие клавиши
  anim/                   плавные анимации
  config/                 конфиг
  gui/NevvaScreen.java    меню: сайдбар, поиск, карточки, настройки
  hud/                    HUD, редактор HUD, ArrayList, TargetHUD, кейстроки,
                          watermark, уведомления
  mixin/                  четыре миксина
  module/modules/         все модули
  module/setting/         тумблеры, слайдеры, списки, цвет
  render/RenderUtils.java сглаженные панели, круги, градиенты, иконки
  ui/                     темы и стили шрифта
```
