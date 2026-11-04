package dev.nevva.visuals.gui;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * NevvaVisuals menu. The whole panel is laid out in a fixed coordinate space and
 * scaled down to fit any screen, so it never gets cut off on a phone.
 */
public class NevvaScreen extends Screen {
	private static final int PANEL_W = 600;
	private static final int PANEL_H = 360;
	private static final int SIDEBAR_W = 110;
	private static final int LIST_W = 226;
	private static final int ROW_H = 26;
	private static final int CONTENT_Y = 52;
	private static final int CONTENT_H = PANEL_H - CONTENT_Y - 18;

	private final NevvaVisuals mod;
	private final Animation open = new Animation(0.0, 12.0);
	private final Map<Module, Animation> hover = new HashMap<>();
	private final Map<Module, Animation> knobs = new HashMap<>();
	private final List<Hit> hits = new ArrayList<>();
	private final List<Ripple> ripples = new ArrayList<>();

	private String search = "";
	private Category category;
	private Module selected;
	private int scroll;
	private boolean dragging;
	private NumberSetting dragSetting;

	public NevvaScreen(NevvaVisuals mod) {
		super(Text.literal("NevvaVisuals"));
		this.mod = mod;
	}

	@Override
	protected void init() {
		open.setValue(0.0);

		if (selected == null) {
			List<Module> modules = mod.getModuleManager().getModules();

			if (!modules.isEmpty()) {
				selected = modules.get(0);
			}
		}
	}

	// ---------------------------------------------------------------- geometry

	private float uiScale() {
		float fit = Math.min((this.width - 8.0f) / PANEL_W, (this.height - 8.0f) / PANEL_H);
		return Math.max(0.35f, Math.min(1.0f, fit));
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

	private List<Category> tabs() {
		List<Category> tabs = new ArrayList<>();
		tabs.add(null);
		tabs.add(Category.HUD);
		tabs.add(Category.RENDER);
		tabs.add(Category.WORLD);
		tabs.add(Category.CLIENT);
		tabs.add(Category.MISC);

		return tabs;
	}

	private List<Module> visibleModules() {
		List<Module> result = new ArrayList<>();

		for (Module module : mod.getModuleManager().getModules()) {
			if (category != null && module.getCategory() != category) {
				continue;
			}

			if (!search.isEmpty() && !module.getName().toLowerCase(Locale.ROOT).contains(search)) {
				continue;
			}

			result.add(module);
		}

		return result;
	}

	// ------------------------------------------------------------------ render

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		open.setTarget(1.0);
		open.update();

		Theme theme = mod.getTheme();
		float scale = uiScale();
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		double t = open.get();

		context.fill(0, 0, this.width, this.height, (int) Math.round(0x96 * t) << 24);

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(originX(), originY(), 0.0f);
		matrices.scale(scale * (float) (0.97 + 0.03 * t), scale * (float) (0.97 + 0.03 * t), 1.0f);

		RenderUtils.glow(context, 0, 0, PANEL_W, PANEL_H, 12, 0x60000000);
		RenderUtils.roundedRect(context, 0, 0, PANEL_W, PANEL_H, 10, theme.background);
		RenderUtils.horizontalGradient(context, 1, 1, PANEL_W - 1, 3, theme.accent, theme.accent2);
		RenderUtils.outline(context, 0, 0, PANEL_W, PANEL_H, theme.border);

		RenderUtils.roundedRect(context, 6, 6, SIDEBAR_W + 4, PANEL_H - 6, 8, theme.sidebar);

		TextRenderer renderer = this.textRenderer;
		drawSidebar(context, renderer, theme, mx, my);
		drawList(context, renderer, theme, mx, my);
		drawSettings(context, renderer, theme, mx, my);
		drawRipples(context, theme);

		matrices.pop();
	}

	private void drawSidebar(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		RenderUtils.text(context, renderer, "Nevva", 14, 12, theme.text);
		RenderUtils.text(context, renderer, "Visuals", 14 + RenderUtils.width(renderer, "Nevva") + 2, 12, theme.accent);
		RenderUtils.textNoShadow(context, renderer, "1.2", 14, 22, theme.dim);

		int sx = 12;
		int sy = 34;
		int sw = SIDEBAR_W - 12;

		RenderUtils.roundedRect(context, sx, sy, sx + sw, sy + 17, 5, theme.column);
		RenderUtils.outline(context, sx, sy, sx + sw, sy + 17, theme.border);
		RenderUtils.textNoShadow(context, renderer, search.isEmpty() ? "Search..." : search, sx + 6, sy + 5,
				search.isEmpty() ? theme.dim : theme.text);

		if (!search.isEmpty() && (System.currentTimeMillis() / 500L) % 2L == 0L) {
			int caret = sx + 6 + renderer.getWidth(search);
			context.fill(caret + 1, sy + 4, caret + 2, sy + 13, theme.accent2);
		}

		List<Category> tabs = tabs();

		for (int i = 0; i < tabs.size(); i++) {
			Category tab = tabs.get(i);
			int ty = 60 + i * 22;
			boolean active = category == tab;
			boolean hovered = mouseX >= sx && mouseX <= sx + sw && mouseY >= ty && mouseY <= ty + 19;

			if (active) {
				RenderUtils.roundedRect(context, sx, ty, sx + sw, ty + 19, 5, theme.card);
				RenderUtils.roundedRect(context, sx, ty + 5, sx + 2, ty + 14, 1, theme.accent);
			} else if (hovered) {
				RenderUtils.roundedRect(context, sx, ty, sx + sw, ty + 19, 5, RenderUtils.withAlpha(theme.card, 0.5));
			}

			int color = active ? theme.text : theme.dim;
			RenderUtils.icon(context, tab == null ? "misc" : tab.getIcon(), sx + 8, ty + 6, active ? theme.accent : color);
			RenderUtils.text(context, renderer, tab == null ? "All" : tab.getLabel(), sx + 22, ty + 6, color);
		}

		int buttonY = PANEL_H - 32;

		if (hovered(sx, buttonY, sx + sw, buttonY + 18, mouseX, mouseY)) {
			RenderUtils.roundedRect(context, sx, buttonY, sx + sw, buttonY + 18, 5, RenderUtils.withAlpha(theme.accent, 0.22));
		}

		RenderUtils.outline(context, sx, buttonY, sx + sw, buttonY + 18, theme.border);
		RenderUtils.text(context, renderer, "Edit HUD", sx + 8, buttonY + 6, theme.text);
	}

	private void drawList(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		int x = SIDEBAR_W + 12;
		int width = LIST_W;
		List<Module> modules = visibleModules();
		int maxScroll = Math.max(0, modules.size() * ROW_H - CONTENT_H);
		scroll = (int) MathHelper.clamp(scroll, 0, maxScroll);

		String title = category == null ? "All modules" : category.getLabel();
		RenderUtils.text(context, renderer, title, x + 2, CONTENT_Y - 18, theme.text);
		RenderUtils.textNoShadow(context, renderer, modules.size() + " total", x + 2 + RenderUtils.width(renderer, title) + 8,
				CONTENT_Y - 18, theme.dim);

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			int ry = CONTENT_Y + 4 + i * ROW_H - scroll;

			if (ry + ROW_H < CONTENT_Y || ry > CONTENT_Y + CONTENT_H) {
				continue;
			}

			Animation rowHover = hover.computeIfAbsent(module, k -> new Animation(0.0, 14.0));
			boolean rowHovered = mouseX >= x && mouseX <= x + width && mouseY >= ry && mouseY <= ry + ROW_H - 4;
			rowHover.setTarget(rowHovered ? 1.0 : 0.0);
			rowHover.update();

			int cardColor = RenderUtils.lerpColor(theme.card, RenderUtils.withAlpha(theme.accent, 0.35), rowHover.get() * 0.6);
			RenderUtils.roundedRect(context, x, ry, x + width, ry + ROW_H - 4, 5, cardColor);

			if (module == selected) {
				RenderUtils.outline(context, x, ry, x + width, ry + ROW_H - 4, RenderUtils.withAlpha(theme.accent, 0.8));
			}

			RenderUtils.text(context, renderer, module.getName(), x + 10, ry + 4, theme.text);
			RenderUtils.textNoShadow(context, renderer, module.getCategory().getLabel(), x + 10, ry + 14, theme.dim);
			drawKnob(context, theme, module, x + width - 36, ry + 7);
		}

		if (modules.isEmpty()) {
			RenderUtils.centeredText(context, renderer, "Nothing found", x + width / 2, CONTENT_Y + 16, theme.dim);
		}
	}

	private void drawKnob(DrawContext context, Theme theme, Module module, int x, int y) {
		Animation animation = knobs.computeIfAbsent(module, k -> new Animation(module.isEnabled() ? 1.0 : 0.0, 16.0));
		animation.setTarget(module.isEnabled() ? 1.0 : 0.0);
		animation.update();

		double v = animation.get();
		RenderUtils.roundedRect(context, x, y, x + 26, y + 11, 5, RenderUtils.lerpColor(theme.track, theme.accent, v));
		int knobX = (int) Math.round(x + 2 + v * 13);
		RenderUtils.circle(context, knobX + 4.5, y + 5.5, 4.5, 0xFFF4F6FB);
	}

	private void drawSettings(DrawContext context, TextRenderer renderer, Theme theme, float mouseX, float mouseY) {
		hits.clear();

		int x = SIDEBAR_W + 12 + LIST_W + 12;
		int width = PANEL_W - x - 12;

		RenderUtils.roundedRect(context, x, CONTENT_Y, x + width, CONTENT_Y + CONTENT_H, 7, theme.column);
		RenderUtils.outline(context, x, CONTENT_Y, x + width, CONTENT_Y + CONTENT_H, theme.border);

		if (selected == null) {
			RenderUtils.centeredText(context, renderer, "Select a module", x + width / 2, CONTENT_Y + 18, theme.dim);
			return;
		}

		RenderUtils.text(context, renderer, selected.getName(), x + 12, CONTENT_Y + 10, theme.text);
		RenderUtils.textNoShadow(context, renderer, selected.getCategory().getLabel(), x + 12,
				CONTENT_Y + 21, theme.accent2);
		RenderUtils.textNoShadow(context, renderer, trim(selected.getDescription(), width - 24, renderer),
				x + 12, CONTENT_Y + 33, theme.dim);

		int y = CONTENT_Y + 50;

		for (Setting<?> setting : selected.getSettings()) {
			if (setting instanceof BoolSetting bool) {
				RenderUtils.text(context, renderer, bool.getLabel(), x + 12, y + 2, theme.text);
				drawStaticKnob(context, theme, x + width - 38, y, bool.get());
				hits.add(new Hit(setting, x + width - 40, y - 2, x + width - 8, y + 14, 1));
				y += 24;
			} else if (setting instanceof NumberSetting number) {
				RenderUtils.text(context, renderer, number.getLabel(), x + 12, y, theme.text);
				String value = format(number.get());
				RenderUtils.textNoShadow(context, renderer, value, x + width - 12 - renderer.getWidth(value), y, theme.accent2);

				int barX1 = x + 12;
				int barX2 = x + width - 12;
				int barY = y + 13;
				double progress = MathHelper.clamp(number.getProgress(), 0.0, 1.0);

				RenderUtils.roundedRect(context, barX1, barY, barX2, barY + 4, 2, theme.track);
				int fill = barX1 + (int) Math.round((barX2 - barX1) * progress);
				RenderUtils.horizontalGradient(context, barX1, barY, Math.max(barX1 + 2, fill), barY + 4,
						theme.accent, theme.accent2);
				RenderUtils.circle(context, fill, barY + 2.0, 3.0, 0xFFF4F6FB);
				hits.add(new Hit(setting, barX1, barY - 6, barX2, barY + 10, 2));
				y += 28;
			} else if (setting instanceof ColorSetting color) {
				RenderUtils.text(context, renderer, color.getLabel(), x + 12, y, theme.text);

				int previewX = x + width - 26;
				RenderUtils.circle(context, previewX + 6, y + 4, 6.0, 0xFF000000 | color.getColor());

				int barX1 = x + 12;
				int barX2 = x + width - 34;
				int barY = y + 13;

				for (int i = 0; i < 24; i++) {
					int segmentX1 = barX1 + (barX2 - barX1) * i / 24;
					int segmentX2 = barX1 + (barX2 - barX1) * (i + 1) / 24 + 1;
					int rgb = ColorSetting.hsvToRgb(i * 15.0f, 0.72f, 1.0f);
					context.fill(segmentX1, barY, segmentX2, barY + 4, 0xFF000000 | rgb);
				}

				int knobX = barX1 + (int) Math.round((barX2 - barX1) * (color.getHue() / 360.0f));
				RenderUtils.circle(context, knobX, barY + 2.0, 3.0, 0xFFFFFFFF);
				hits.add(new Hit(setting, barX1, barY - 6, barX2, barY + 10, 4));
				y += 28;
			} else if (setting instanceof ChoiceSetting choice) {
				RenderUtils.text(context, renderer, choice.getLabel(), x + 12, y + 2, theme.text);

				String value = choice.getValue();
				int valueWidth = renderer.getWidth(value);
				int valueX = x + width - 12 - valueWidth;
				RenderUtils.textNoShadow(context, renderer, "<", valueX - 8, y + 2, theme.dim);
				RenderUtils.textNoShadow(context, renderer, value, valueX, y + 2, theme.accent2);
				RenderUtils.textNoShadow(context, renderer, ">", valueX + valueWidth + 3, y + 2, theme.dim);
				hits.add(new Hit(setting, valueX - 12, y - 2, x + width - 8, y + 14, 3));
				y += 24;
			}
		}

	}

	private void drawStaticKnob(DrawContext context, Theme theme, int x, int y, boolean on) {
		RenderUtils.roundedRect(context, x, y, x + 26, y + 11, 5, on ? theme.accent : theme.track);
		int knobX = on ? x + 15 : x + 2;
		RenderUtils.circle(context, knobX + 4.5, y + 5.5, 4.5, 0xFFF4F6FB);
	}

	private void drawRipples(DrawContext context, Theme theme) {
		long now = System.currentTimeMillis();

		for (int i = ripples.size() - 1; i >= 0; i--) {
			Ripple ripple = ripples.get(i);
			double age = (now - ripple.start) / 320.0;

			if (age >= 1.0) {
				ripples.remove(i);
				continue;
			}

			RenderUtils.circle(context, ripple.x, ripple.y, 6 + age * 34, RenderUtils.withAlpha(theme.accent, (1.0 - age) * 0.22));
		}
	}

	private boolean hovered(int x1, int y1, int x2, int y2, float mouseX, float mouseY) {
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	private String trim(String text, int maxWidth, TextRenderer renderer) {
		if (renderer.getWidth(text) <= maxWidth) {
			return text;
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			String next = builder.toString() + text.charAt(i);

			if (renderer.getWidth(next + "...") > maxWidth) {
				break;
			}

			builder.append(text.charAt(i));
		}

		return builder + "...";
	}

	private String format(double value) {
		return Math.abs(value - Math.rint(value)) < 0.001
				? String.valueOf((long) Math.rint(value))
				: String.format(Locale.ROOT, "%.1f", value);
	}

	// ------------------------------------------------------------------- input

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (chr >= 32 && chr != 127) {
			search = (search + Character.toLowerCase(chr)).trim();

			if (search.length() > 16) {
				search = search.substring(0, 16);
			}

			scroll = 0;
			return true;
		}

		return super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 259 && !search.isEmpty()) {
			search = search.substring(0, search.length() - 1);
			scroll = 0;
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		int sx = 12;
		int sw = SIDEBAR_W - 12;

		List<Category> tabs = tabs();

		for (int i = 0; i < tabs.size(); i++) {
			int ty = 60 + i * 22;

			if (hovered(sx, ty, sx + sw, ty + 19, mx, my)) {
				category = tabs.get(i);
				scroll = 0;
				return true;
			}
		}

		int buttonY = PANEL_H - 32;

		if (hovered(sx, buttonY, sx + sw, buttonY + 18, mx, my)) {
			mod.saveConfig();
			mod.openHudEditor();
			return true;
		}

		int x = SIDEBAR_W + 12;
		List<Module> modules = visibleModules();

		for (int i = 0; i < modules.size(); i++) {
			int ry = CONTENT_Y + 4 + i * ROW_H - scroll;

			if (ry < CONTENT_Y || ry + ROW_H - 4 > CONTENT_Y + CONTENT_H) {
				continue;
			}

			if (hovered(x, ry, x + LIST_W, ry + ROW_H - 4, mx, my)) {
				Module module = modules.get(i);
				selected = module;
				module.toggle();
				ripples.add(new Ripple(mx, my));
				return true;
			}
		}

		for (Hit hit : hits) {
			if (hovered(hit.x1, hit.y1, hit.x2, hit.y2, mx, my)) {
				applyHit(hit, mx);
				ripples.add(new Ripple(mx, my));
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void applyHit(Hit hit, float mouseX) {
		if (hit.type == 1 && hit.setting instanceof BoolSetting bool) {
			bool.toggle();
			mod.saveConfig();
		} else if (hit.type == 2 && hit.setting instanceof NumberSetting number) {
			dragging = true;
			dragSetting = number;
			number.setProgress((mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1));
			mod.saveConfig();
		} else if (hit.type == 3 && hit.setting instanceof ChoiceSetting choice) {
			choice.cycle(1);
			mod.saveConfig();
		} else if (hit.type == 4 && hit.setting instanceof ColorSetting color) {
			dragging = true;
			color.setHue((float) ((mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1) * 360.0));
			mod.saveConfig();
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (dragging && dragSetting != null) {
			for (Hit hit : hits) {
				if (hit.setting == dragSetting && hit.type == 2) {
					dragSetting.setProgress((uiX(mouseX) - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1));
					mod.saveConfig();
					break;
				}
			}

			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		dragging = false;
		dragSetting = null;

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		float mx = uiX(mouseX);
		float my = uiY(mouseY);
		int x = SIDEBAR_W + 12;

		if (hovered(x, CONTENT_Y, x + LIST_W, CONTENT_Y + CONTENT_H, mx, my)) {
			List<Module> modules = visibleModules();
			int maxScroll = Math.max(0, modules.size() * ROW_H - CONTENT_H);
			scroll = (int) MathHelper.clamp(scroll - verticalAmount * 16.0, 0, maxScroll);

			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private static final class Hit {
		final Setting<?> setting;
		final int x1;
		final int y1;
		final int x2;
		final int y2;
		final int type;

		Hit(Setting<?> setting, int x1, int y1, int x2, int y2, int type) {
			this.setting = setting;
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
