package dev.nevva.visuals.gui;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.modules.InterfaceModule;
import dev.nevva.visuals.module.modules.MusicWidgetModule;
import dev.nevva.visuals.module.modules.TargetHudModule;
import dev.nevva.visuals.module.modules.WatermarkModule;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.module.setting.NumberSetting;
import dev.nevva.visuals.module.setting.Setting;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Responsive glass-style configuration menu. */
public class NevvaScreen extends Screen {
	private static final int PANEL_W = 720;
	private static final int PANEL_H = 420;
	private static final int SIDEBAR_W = 136;
	private static final int LIST_X = 148;
	private static final int LIST_W = 245;
	private static final int DETAIL_X = 405;
	private static final int DETAIL_W = 303;
	private static final int LIST_TOP = 96;
	private static final int LIST_BOTTOM = 397;
	private static final int ROW_STEP = 49;
	private static final int ROW_H = 44;

	private final NevvaVisuals mod;
	private final Animation open = new Animation(0.0, 12.0);
	private final Map<Module, Animation> hover = new HashMap<>();
	private final Map<Module, Animation> knobs = new HashMap<>();
	private final List<Hit> hits = new ArrayList<>();
	private final List<Ripple> ripples = new ArrayList<>();

	private String search = "";
	private Category category = Category.RENDER;
	private Module selected;
	private Module awaitingBind;
	private Hit dragHit;
	private int scroll;
	private boolean searchFocused;
	private boolean settingsOpen;

	public NevvaScreen(NevvaVisuals mod) {
		super(Text.literal("shadow"));
		this.mod = mod;
	}

	@Override
	protected void init() {
		open.setValue(0.0);
	}

	private float uiScale() {
		float fit = Math.min((this.width - 12.0f) / PANEL_W, (this.height - 12.0f) / PANEL_H);
		return Math.max(0.2f, Math.min(1.0f, fit));
	}

	private float originX() {
		return (this.width - PANEL_W * uiScale()) / 2.0f;
	}

	private float originY() {
		return (this.height - PANEL_H * uiScale()) / 2.0f;
	}

	private float uiX(double mouseX) {
		return (float) ((mouseX - originX()) / uiScale());
	}

	private float uiY(double mouseY) {
		return (float) ((mouseY - originY()) / uiScale());
	}

	private List<Category> categories() {
		return List.of(Category.COMBAT, Category.RENDER, Category.MOVEMENT);
	}

	private Category menuCategory(Category moduleCategory) {
		if (moduleCategory == Category.COMBAT || moduleCategory == Category.MOVEMENT) {
			return moduleCategory;
		}

		return Category.RENDER;
	}

	private List<Module> visibleModules() {
		List<Module> result = new ArrayList<>();
		String query = search.toLowerCase(Locale.ROOT);

		for (Module module : mod.getModuleManager().getModules()) {
			if (menuCategory(module.getCategory()) != category) {
				continue;
			}

			if (!query.isEmpty()
					&& !module.getName().toLowerCase(Locale.ROOT).contains(query)
					&& !module.getDescription().toLowerCase(Locale.ROOT).contains(query)) {
				continue;
			}

			result.add(module);
		}

		return result;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		open.setTarget(1.0);
		open.update();

		Theme theme = mod.getTheme();
		float scale = uiScale();
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		hits.clear();

		context.fill(0, 0, this.width, this.height, RenderUtils.withAlpha(0xFF030812, 0.72 * open.get()));

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(originX(), originY(), 0.0f);
		matrices.scale(scale, scale, 1.0f);

		RenderUtils.glow(context, 0, 0, PANEL_W, PANEL_H, 15,
				RenderUtils.withAlpha(theme.accent, 0.18 * open.get()));
		RenderUtils.panel(context, 0, 0, PANEL_W, PANEL_H, 12, theme.background,
				RenderUtils.withAlpha(theme.border, 0.95));
		RenderUtils.gradientRect(context, 1, 1, PANEL_W - 1, PANEL_H - 1,
				RenderUtils.withAlpha(theme.accent, 0.035), 0x00000000);
		RenderUtils.roundedRect(context, 6, 6, SIDEBAR_W + 3, PANEL_H - 6, 9, theme.sidebar);
		RenderUtils.outline(context, 6, 6, SIDEBAR_W + 3, PANEL_H - 6, RenderUtils.withAlpha(theme.border, 0.75));

		TextRenderer renderer = this.textRenderer;
		drawSidebar(context, renderer, theme, mx, my);
		drawHeader(context, renderer, theme, mx, my);
		drawModuleList(context, renderer, theme, mx, my);
		drawDetail(context, renderer, theme, mx, my);
		drawRipples(context, theme);

		matrices.pop();
	}

	private void drawSidebar(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		RenderUtils.roundedRect(context, 14, 15, 38, 39, 7, RenderUtils.withAlpha(theme.accent, 0.22));
		RenderUtils.outline(context, 14, 15, 38, 39, RenderUtils.withAlpha(theme.accent, 0.8));
		RenderUtils.centeredText(context, renderer, "N", 26, 21, theme.accent2);
		RenderUtils.text(context, renderer, "shadow", 45, 16, theme.text);
		RenderUtils.textNoShadow(context, renderer, "VISUAL CLIENT", 45, 28, theme.dim);

		RenderUtils.roundedRect(context, 12, 53, SIDEBAR_W - 7, 76, 6, theme.column);
		RenderUtils.outline(context, 12, 53, SIDEBAR_W - 7, 76, theme.border);
		String searchDisplay = search.isEmpty() ? "Search modules" : trim(search, SIDEBAR_W - 34, renderer);
		RenderUtils.textNoShadow(context, renderer, searchDisplay, 20, 62,
				search.isEmpty() ? theme.dim : theme.text);

		if (searchFocused && (System.currentTimeMillis() / 500L) % 2L == 0L) {
			int caret = 20 + RenderUtils.width(renderer, searchDisplay);
			context.fill(caret + 2, 60, caret + 3, 70, theme.accent2);
		}

		RenderUtils.textNoShadow(context, renderer, "CATEGORIES", 15, 91, theme.dim);
		List<Category> categories = categories();

		for (int i = 0; i < categories.size(); i++) {
			Category tab = categories.get(i);
			int y = 106 + i * 34;
			boolean active = category == tab;
			boolean over = hovered(12, y, SIDEBAR_W - 7, y + 28, mouseX, mouseY);

			if (active) {
				RenderUtils.roundedRect(context, 12, y, SIDEBAR_W - 7, y + 28, 6,
						RenderUtils.withAlpha(theme.accent, 0.18));
				RenderUtils.roundedRect(context, 12, y + 7, 14, y + 21, 1, theme.accent);
			} else if (over) {
				RenderUtils.roundedRect(context, 12, y, SIDEBAR_W - 7, y + 28, 6,
						RenderUtils.withAlpha(theme.card, 0.75));
			}

			int color = active ? theme.text : theme.dim;
			RenderUtils.icon(context, tab.getIcon(), 23, y + 9, active ? theme.accent2 : color);
			RenderUtils.text(context, renderer, tab.getLabel(), 39, y + 10, color);
		}

		int hudY = PANEL_H - 42;
		boolean hudHover = hovered(12, hudY, SIDEBAR_W - 7, hudY + 28, mouseX, mouseY);
		RenderUtils.roundedRect(context, 12, hudY, SIDEBAR_W - 7, hudY + 28, 6,
				 hudHover ? RenderUtils.withAlpha(theme.accent, 0.18) : theme.column);
		RenderUtils.outline(context, 12, hudY, SIDEBAR_W - 7, hudY + 28, theme.border);
		RenderUtils.text(context, renderer, "Edit HUD", 22, hudY + 10, theme.text);
		RenderUtils.textNoShadow(context, renderer, "ESC  CLOSE", 16, PANEL_H - 14, theme.dim);
	}

	private void drawHeader(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		String title = category.getLabel();
		RenderUtils.text(context, renderer, title, LIST_X, 14, theme.text);
		int titleWidth = RenderUtils.width(renderer, title);

		RenderUtils.roundedRect(context, LIST_X + titleWidth + 10, 13, LIST_X + titleWidth + 48, 26, 5,
				RenderUtils.withAlpha(theme.accent, 0.2));
		RenderUtils.textNoShadow(context, renderer, "BETA", LIST_X + titleWidth + 17, 16, theme.accent2);
		RenderUtils.textNoShadow(context, renderer, "Visual modules and controls", LIST_X, 31, theme.dim);

		InterfaceModule ui = mod.getModuleManager().get(InterfaceModule.class);
		ChoiceSetting themeSetting = ui == null ? null : ui.getTheme();
		String themeName = themeSetting == null ? "Nursultan" : themeSetting.getValue();
		int x = PANEL_W - 164;
		boolean over = hovered(x, 13, PANEL_W - 12, 39, mouseX, mouseY);
		RenderUtils.roundedRect(context, x, 13, PANEL_W - 12, 39, 6,
				over ? RenderUtils.withAlpha(theme.accent, 0.22) : theme.column);
		RenderUtils.outline(context, x, 13, PANEL_W - 12, 39, theme.border);
		RenderUtils.circle(context, x + 12, 26, 3.0, theme.accent);
		RenderUtils.textNoShadow(context, renderer, "THEME", x + 22, 18, theme.dim);
		RenderUtils.text(context, renderer, themeName, x + 22, 28, theme.text);
		RenderUtils.textNoShadow(context, renderer, "v", PANEL_W - 27, 23, theme.accent2);
	}

	private void drawModuleList(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		RenderUtils.panel(context, LIST_X, 52, LIST_X + LIST_W, PANEL_H - 14, 8, theme.column, theme.border);
		List<Module> modules = visibleModules();
		int listHeight = LIST_BOTTOM - LIST_TOP;
		int maxScroll = Math.max(0, modules.size() * ROW_STEP - listHeight);
		scroll = (int) MathHelper.clamp(scroll, 0, maxScroll);

		RenderUtils.text(context, renderer, "Modules", LIST_X + 12, 63, theme.text);
		String count = String.valueOf(modules.size());
		RenderUtils.textNoShadow(context, renderer, count, LIST_X + LIST_W - 22 - renderer.getWidth(count), 64, theme.dim);
		RenderUtils.textNoShadow(context, renderer, "LEFT CLICK  TOGGLE  ·  RIGHT CLICK  SETTINGS",
				LIST_X + 12, 78, theme.dim);

		context.enableScissor(LIST_X + 1, LIST_TOP, LIST_X + LIST_W - 1, LIST_BOTTOM);
		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			int y = LIST_TOP + i * ROW_STEP - scroll;

			if (y + ROW_H < LIST_TOP || y > LIST_BOTTOM) {
				continue;
			}

			Animation animation = hover.computeIfAbsent(module, key -> new Animation(0.0, 14.0));
			boolean over = hovered(LIST_X + 9, y, LIST_X + LIST_W - 9, y + ROW_H - 3, mouseX, mouseY);
			animation.setTarget(over ? 1.0 : 0.0);
			animation.update();

			int left = LIST_X + 9;
			int right = LIST_X + LIST_W - 9;
			int bottom = y + ROW_H - 3;
			int fill = RenderUtils.lerpColor(theme.card, RenderUtils.withAlpha(theme.accent, 0.28), animation.get() * 0.7);
			RenderUtils.roundedRect(context, left, y, right, bottom, 6, fill);
			RenderUtils.outline(context, left, y, right, bottom,
					module == selected && settingsOpen ? RenderUtils.withAlpha(theme.accent, 0.9) : theme.border);

			RenderUtils.text(context, renderer, module.getName(), left + 11, y + 8, theme.text);
			String description = trim(module.getDescription(), LIST_W - 74, renderer);
			RenderUtils.textNoShadow(context, renderer, description, left + 11, y + 23, theme.dim);
			drawKnob(context, theme, module, right - 37, y + 9);

			if (module.getKeyCode() >= 0) {
				String key = bindName(module);
				RenderUtils.textNoShadow(context, renderer, key, right - 12 - renderer.getWidth(key), y + 24, theme.accent2);
			}
		}
		context.disableScissor();

		if (modules.isEmpty()) {
			RenderUtils.centeredText(context, renderer, "No matching modules", LIST_X + LIST_W / 2, LIST_TOP + 16, theme.dim);
		}
	}

	private void drawKnob(DrawContext context, Theme theme, Module module, int x, int y) {
		Animation animation = knobs.computeIfAbsent(module, key -> new Animation(module.isEnabled() ? 1.0 : 0.0, 16.0));
		animation.setTarget(module.isEnabled() ? 1.0 : 0.0);
		animation.update();

		double value = animation.get();
		RenderUtils.roundedRect(context, x, y, x + 26, y + 12,
				6, RenderUtils.lerpColor(theme.track, theme.accent, value));
		int knobX = (int) Math.round(x + 2 + value * 12);
		RenderUtils.circle(context, knobX + 4, y + 6, 4.0, 0xFFF4F8FF);
	}

	private void drawDetail(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		RenderUtils.panel(context, DETAIL_X, 52, DETAIL_X + DETAIL_W, PANEL_H - 14, 8, theme.column, theme.border);

		if (settingsOpen && selected != null) {
			drawSettings(context, renderer, theme, selected, mouseX, mouseY);
		} else {
			drawDashboard(context, renderer, theme, mouseX, mouseY);
		}
	}

	private void drawSettings(DrawContext context, TextRenderer renderer, Theme theme, Module module,
			float mouseX, float mouseY) {
		int x = DETAIL_X + 13;
		int right = DETAIL_X + DETAIL_W - 13;
		RenderUtils.text(context, renderer, module.getName(), x, 64, theme.text);
		RenderUtils.textNoShadow(context, renderer, "MODULE SETTINGS", x, 78, theme.accent2);
		RenderUtils.textNoShadow(context, renderer, "×", right - 9, 64, theme.dim);
		RenderUtils.textNoShadow(context, renderer, trim(module.getDescription(), DETAIL_W - 28, renderer), x, 91, theme.dim);

		int bindY = 109;
		RenderUtils.text(context, renderer, "Keybind", x, bindY + 7, theme.text);
		String bindLabel = awaitingBind == module ? "Press a key..." : bindName(module);
		int bindWidth = Math.max(84, renderer.getWidth(bindLabel) + 18);
		int bindX = right - bindWidth;
		boolean bindHover = hovered(bindX, bindY, right, bindY + 24, mouseX, mouseY);
		RenderUtils.roundedRect(context, bindX, bindY, right, bindY + 24, 5,
				bindHover ? RenderUtils.withAlpha(theme.accent, 0.22) : theme.card);
		RenderUtils.outline(context, bindX, bindY, right, bindY + 24, theme.border);
		RenderUtils.centeredText(context, renderer, bindLabel, bindX + bindWidth / 2, bindY + 8,
				awaitingBind == module ? theme.accent2 : theme.text);
		hits.add(new Hit(module, bindX, bindY, right, bindY + 24, 5));

		RenderUtils.textNoShadow(context, renderer, "SETTINGS", x, 145, theme.dim);
		int y = 159;

		for (Setting<?> setting : module.getSettings()) {
			if (y > PANEL_H - 31) {
				break;
			}

			if (setting instanceof BoolSetting bool) {
				RenderUtils.text(context, renderer, bool.getLabel(), x, y + 4, theme.text);
				drawStaticKnob(context, theme, right - 28, y + 1, bool.get());
				hits.add(new Hit(setting, x, y, right, y + 21, 1));
				y += 29;
			} else if (setting instanceof NumberSetting number) {
				RenderUtils.text(context, renderer, number.getLabel(), x, y, theme.text);
				String value = format(number.get());
				RenderUtils.textNoShadow(context, renderer, value, right - renderer.getWidth(value), y, theme.accent2);
				int barY = y + 18;
				drawSlider(context, theme, x, right, barY, number.getProgress());
				hits.add(new Hit(setting, x, barY - 5, right, barY + 8, 2));
				y += 39;
			} else if (setting instanceof ChoiceSetting choice) {
				RenderUtils.text(context, renderer, choice.getLabel(), x, y + 4, theme.text);
				String value = choice.getValue();
				int valueWidth = renderer.getWidth(value);
				int valueX = right - valueWidth;
				RenderUtils.textNoShadow(context, renderer, "<", valueX - 12, y + 4, theme.dim);
				RenderUtils.textNoShadow(context, renderer, value, valueX, y + 4, theme.accent2);
				RenderUtils.textNoShadow(context, renderer, ">", right + 1, y + 4, theme.dim);
				hits.add(new Hit(setting, x, y, right, y + 21, 3));
				y += 29;
			} else if (setting instanceof ColorSetting color) {
				RenderUtils.text(context, renderer, color.getLabel(), x, y, theme.text);
				int barY = y + 18;
				int barRight = right - 12;
				for (int i = 0; i < 30; i++) {
					int x1 = x + (barRight - x) * i / 30;
					int x2 = x + (barRight - x) * (i + 1) / 30 + 1;
					context.fill(x1, barY, x2, barY + 5, 0xFF000000 | ColorSetting.hsvToRgb(i * 12.0f, 0.78f, 1.0f));
				}
				int knobX = x + (int) Math.round((barRight - x) * color.getHue() / 360.0f);
				RenderUtils.circle(context, knobX, barY + 2.5, 3.5, 0xFFFFFFFF);
				RenderUtils.circle(context, right - 5, y + 3, 4.0, 0xFF000000 | color.getColor());
				hits.add(new Hit(setting, x, barY - 5, barRight, barY + 9, 4));
				y += 39;
			}
		}
	}

	private void drawDashboard(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		int x = DETAIL_X + 13;
		int right = DETAIL_X + DETAIL_W - 13;
		RenderUtils.text(context, renderer, "Quick controls", x, 64, theme.text);
		RenderUtils.textNoShadow(context, renderer, "VISUAL HUD", x, 78, theme.accent2);

		WatermarkModule watermark = mod.getModuleManager().get(WatermarkModule.class);
		TargetHudModule target = mod.getModuleManager().get(TargetHudModule.class);
		drawQuickToggle(context, renderer, theme, watermark, x, 91, right, mouseX, mouseY);
		drawQuickToggle(context, renderer, theme, target, x, 134, right, mouseX, mouseY);

		MusicWidgetModule music = mod.getModuleManager().get(MusicWidgetModule.class);
		if (music == null) {
			return;
		}

		int top = 181;
		RenderUtils.roundedRect(context, x, top, right, PANEL_H - 26, 7, theme.card);
		if (!music.isEnabled()) {
			RenderUtils.outline(context, x, top, right, PANEL_H - 26, theme.border);
			RenderUtils.text(context, renderer, "Music widget is disabled", x + 11, top + 34, theme.text);
			RenderUtils.textNoShadow(context, renderer, "Enable it from the module list to use its controls.",
					x + 11, top + 51, theme.dim);
			return;
		}
		RenderUtils.outline(context, x, top, right, PANEL_H - 26, theme.border);
		RenderUtils.textNoShadow(context, renderer, "MUSIC PLAYER", x + 11, top + 12, theme.dim);
		RenderUtils.roundedRect(context, right - 70, top + 7, right - 9, top + 20, 5,
				RenderUtils.withAlpha(theme.accent, 0.16));
		RenderUtils.centeredText(context, renderer, "UI PREVIEW", right - 39, top + 10, theme.accent2);
		RenderUtils.text(context, renderer, "Midnight Signal", x + 11, top + 34, theme.text);
		RenderUtils.textNoShadow(context, renderer, "shadow radio  ·  instrumental", x + 11, top + 47, theme.dim);

		int progressY = top + 68;
		drawSlider(context, theme, x + 11, right - 11, progressY, music.getProgress().getProgress());
		String percent = format(music.getProgress().get()) + "%";
		RenderUtils.textNoShadow(context, renderer, percent, right - 11 - renderer.getWidth(percent), progressY + 9, theme.dim);
		hits.add(new Hit(music.getProgress(), x + 11, progressY - 5, right - 11, progressY + 8, 2));

		int buttonY = top + 93;
		String playback = music.getPlaying().get() ? "PAUSE PREVIEW" : "PLAY PREVIEW";
		RenderUtils.roundedRect(context, x + 11, buttonY, right - 11, buttonY + 25, 5,
				RenderUtils.withAlpha(theme.accent, 0.2));
		RenderUtils.outline(context, x + 11, buttonY, right - 11, buttonY + 25, RenderUtils.withAlpha(theme.accent, 0.55));
		RenderUtils.centeredText(context, renderer, playback, (x + right) / 2, buttonY + 9, theme.text);
		hits.add(new Hit(music.getPlaying(), x + 11, buttonY, right - 11, buttonY + 25, 1));

		int bassY = top + 131;
		RenderUtils.text(context, renderer, "BassBoost", x + 11, bassY, theme.text);
		String bass = format(music.getBassBoost().get()) + "%";
		RenderUtils.textNoShadow(context, renderer, bass, right - 11 - renderer.getWidth(bass), bassY, theme.accent2);
		int bassBarY = bassY + 16;
		drawSlider(context, theme, x + 11, right - 11, bassBarY, music.getBassBoost().getProgress());
		hits.add(new Hit(music.getBassBoost(), x + 11, bassBarY - 5, right - 11, bassBarY + 8, 2));
		RenderUtils.textNoShadow(context, renderer, "Controls are visual only; no audio is played.", x + 11, bassBarY + 17, theme.dim);
	}

	private void drawQuickToggle(DrawContext context, TextRenderer renderer, Theme theme, Module module,
			int x, int y, int right, float mouseX, float mouseY) {
		if (module == null) {
			return;
		}

		boolean over = hovered(x, y, right, y + 36, mouseX, mouseY);
		RenderUtils.roundedRect(context, x, y, right, y + 36, 6,
				over ? RenderUtils.withAlpha(theme.accent, 0.13) : theme.card);
		RenderUtils.outline(context, x, y, right, y + 36, theme.border);
		RenderUtils.text(context, renderer, module.getName(), x + 10, y + 7, theme.text);
		RenderUtils.textNoShadow(context, renderer, "HUD widget", x + 10, y + 20, theme.dim);
		drawKnob(context, theme, module, right - 36, y + 12);
		hits.add(new Hit(module, x, y, right, y + 36, 6));
	}

	private void drawSlider(DrawContext context, Theme theme, int x1, int x2, int y, double progress) {
		double value = Math.max(0.0, Math.min(1.0, progress));
		RenderUtils.roundedRect(context, x1, y, x2, y + 4, 2, theme.track);
		int fill = x1 + (int) Math.round((x2 - x1) * value);
		if (fill > x1) {
			RenderUtils.horizontalGradient(context, x1, y, fill, y + 4, theme.accent, theme.accent2);
		}
		RenderUtils.circle(context, fill, y + 2.0, 3.0, 0xFFF4F8FF);
	}

	private void drawStaticKnob(DrawContext context, Theme theme, int x, int y, boolean on) {
		RenderUtils.roundedRect(context, x, y, x + 26, y + 12, 6, on ? theme.accent : theme.track);
		int knobX = on ? x + 14 : x + 2;
		RenderUtils.circle(context, knobX + 4, y + 6, 4.0, 0xFFF4F8FF);
	}

	private void drawRipples(DrawContext context, Theme theme) {
		long now = System.currentTimeMillis();

		for (int i = ripples.size() - 1; i >= 0; i--) {
			Ripple ripple = ripples.get(i);
			double age = (now - ripple.start) / 340.0;

			if (age >= 1.0) {
				ripples.remove(i);
				continue;
			}

			RenderUtils.circle(context, ripple.x, ripple.y, 5 + age * 30,
					RenderUtils.withAlpha(theme.accent, (1.0 - age) * 0.18));
		}
	}

	private String bindName(Module module) {
		if (module == null || module.getKeyCode() < 0) {
			return "Unbound";
		}

		return InputUtil.fromKeyCode(module.getKeyCode(), 0).getLocalizedText().getString();
	}

	private String trim(String text, int maxWidth, TextRenderer renderer) {
		if (renderer.getWidth(text) <= maxWidth) {
			return text;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			String next = builder.toString() + text.charAt(i);
			if (renderer.getWidth(next + "…") > maxWidth) {
				break;
			}
			builder.append(text.charAt(i));
		}

		return builder + "…";
	}

	private String format(double value) {
		return Math.abs(value - Math.rint(value)) < 0.001
				? String.valueOf((long) Math.rint(value))
				: String.format(Locale.ROOT, "%.1f", value);
	}

	private boolean hovered(int x1, int y1, int x2, int y2, float mouseX, float mouseY) {
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (searchFocused && chr >= 32 && chr != 127) {
			search += Character.toLowerCase(chr);
			if (search.length() > 32) {
				search = search.substring(0, 32);
			}
			scroll = 0;
			return true;
		}

		return super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (awaitingBind != null) {
			awaitingBind.setKeyCode(keyCode == GLFW.GLFW_KEY_ESCAPE ? -1 : keyCode);
			awaitingBind = null;
			mod.saveConfig();
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_ESCAPE && settingsOpen) {
			settingsOpen = false;
			searchFocused = false;
			return true;
		}

		if (searchFocused && keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
			search = search.substring(0, search.length() - 1);
			scroll = 0;
			return true;
		}

		if (searchFocused && keyCode == GLFW.GLFW_KEY_ENTER) {
			searchFocused = false;
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		searchFocused = button == 0 && hovered(12, 53, SIDEBAR_W - 7, 76, mx, my);

		if (button != 0 && button != 1) {
			return super.mouseClicked(mouseX, mouseY, button);
		}
		if (button == 0 && searchFocused) {
			return true;
		}

		if (button == 0) {
			List<Category> categories = categories();
			for (int i = 0; i < categories.size(); i++) {
				int y = 106 + i * 34;
				if (hovered(12, y, SIDEBAR_W - 7, y + 28, mx, my)) {
					category = categories.get(i);
					scroll = 0;
					settingsOpen = false;
					return true;
				}
			}

			int themeX = PANEL_W - 164;
			if (hovered(themeX, 13, PANEL_W - 12, 39, mx, my)) {
				InterfaceModule ui = mod.getModuleManager().get(InterfaceModule.class);
				if (ui != null) {
					ui.getTheme().cycle(1);
					mod.saveConfig();
				}
				return true;
			}

			int hudY = PANEL_H - 42;
			if (hovered(12, hudY, SIDEBAR_W - 7, hudY + 28, mx, my)) {
				mod.saveConfig();
				mod.openHudEditor();
				return true;
			}
		}

		List<Module> modules = visibleModules();
		for (int i = 0; i < modules.size(); i++) {
			int y = LIST_TOP + i * ROW_STEP - scroll;
			int rowTop = Math.max(y, LIST_TOP);
			int rowBottom = Math.min(y + ROW_H - 3, LIST_BOTTOM);
			if (rowBottom <= rowTop) {
				continue;
			}

			if (hovered(LIST_X + 9, rowTop, LIST_X + LIST_W - 9, rowBottom, mx, my)) {
				Module module = modules.get(i);
				selected = module;
				if (button == 1) {
					settingsOpen = true;
				} else {
					settingsOpen = false;
					module.toggle();
					ripples.add(new Ripple(mx, my));
				}
				return true;
			}
		}

		for (Hit hit : hits) {
			if (!hovered(hit.x1, hit.y1, hit.x2, hit.y2, mx, my)) {
				continue;
			}

			if (hit.type == 6 && hit.target instanceof Module module) {
				if (button == 1) {
					selected = module;
					settingsOpen = true;
				} else {
					module.toggle();
					mod.saveConfig();
				}
				return true;
			}

			if (button == 1) {
				return true;
			}

			applyHit(hit, mx);
			ripples.add(new Ripple(mx, my));
			return true;
		}

		if (settingsOpen && button == 0 && hovered(DETAIL_X + DETAIL_W - 25, 58,
				DETAIL_X + DETAIL_W - 10, 79, mx, my)) {
			settingsOpen = false;
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void applyHit(Hit hit, float mouseX) {
		if (hit.type == 5 && hit.target instanceof Module module) {
			awaitingBind = module;
			return;
		}

		if (hit.target instanceof BoolSetting bool) {
			bool.toggle();
			mod.saveConfig();
		} else if (hit.target instanceof NumberSetting number && hit.type == 2) {
			dragHit = hit;
			updateNumber(number, hit, mouseX);
		} else if (hit.target instanceof ChoiceSetting choice) {
			choice.cycle(mouseX < (hit.x1 + hit.x2) / 2.0f ? -1 : 1);
			mod.saveConfig();
		} else if (hit.target instanceof ColorSetting color && hit.type == 4) {
			dragHit = hit;
			updateColor(color, hit, mouseX);
		}
	}

	private void updateNumber(NumberSetting setting, Hit hit, float mouseX) {
		setting.setProgress((mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1));
	}

	private void updateColor(ColorSetting setting, Hit hit, float mouseX) {
		setting.setHue((float) ((mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1) * 360.0));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (dragHit != null && button == 0) {
			float mx = uiX(mouseX);
			if (dragHit.target instanceof NumberSetting number) {
				updateNumber(number, dragHit, mx);
			} else if (dragHit.target instanceof ColorSetting color) {
				updateColor(color, dragHit, mx);
			}
			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragHit != null) {
			dragHit = null;
			mod.saveConfig();
			return true;
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		if (hovered(LIST_X, LIST_TOP, LIST_X + LIST_W, LIST_BOTTOM, mx, my)) {
			List<Module> modules = visibleModules();
			int maxScroll = Math.max(0, modules.size() * ROW_STEP - (LIST_BOTTOM - LIST_TOP));
			scroll = (int) MathHelper.clamp(scroll - verticalAmount * 18.0, 0, maxScroll);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private static final class Hit {
		final Object target;
		final int x1;
		final int y1;
		final int x2;
		final int y2;
		final int type;

		Hit(Object target, int x1, int y1, int x2, int y2, int type) {
			this.target = target;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.type = type;
		}
	}

	private static final class Ripple {
		final float x;
		final float y;
		final long start = System.currentTimeMillis();

		Ripple(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}
