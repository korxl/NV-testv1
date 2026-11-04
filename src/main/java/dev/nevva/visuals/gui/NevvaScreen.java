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
 * NevvaVisuals menu: a sidebar with the logo, search and categories, the module
 * list in the middle and the settings of the selected module on the right.
 */
public class NevvaScreen extends Screen {
	private static final int PANEL_W = 620;
	private static final int PANEL_H = 360;
	private static final int SIDEBAR_W = 132;
	private static final int LIST_W = 210;
	private static final int ROW_H = 26;

	private static final int PANEL_COLOR = 0xF00A0C12;
	private static final int SIDEBAR_COLOR = 0xFF0D1017;
	private static final int COLUMN_COLOR = 0xFF101620;
	private static final int BORDER_COLOR = 0xFF1E2430;
	private static final int TEXT_COLOR = 0xFFE6EAF2;
	private static final int TEXT_DIM = 0xFF7C8698;
	private static final int ACCENT = 0xFF8B5CF6;
	private static final int ACCENT_2 = 0xFF22D3EE;
	private static final int TRACK_COLOR = 0xFF232B38;

	private final NevvaVisuals mod;
	private final Animation open = new Animation(0.0, 12.0);
	private final Map<Module, Animation> hover = new HashMap<>();
	private final Map<Module, Animation> knobs = new HashMap<>();
	private final List<Hit> hits = new ArrayList<>();

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

	private int panelX() {
		return (this.width - PANEL_W) / 2;
	}

	private int panelY() {
		return (this.height - PANEL_H) / 2;
	}

	private int sidebarX() {
		return panelX() + 1;
	}

	private int contentX() {
		return panelX() + SIDEBAR_W + 8;
	}

	private int listX() {
		return contentX();
	}

	private int listY() {
		return panelY() + 58;
	}

	private int listH() {
		return PANEL_H - 58 - 26;
	}

	private int settingsX() {
		return listX() + LIST_W + 10;
	}

	private int settingsW() {
		return panelX() + PANEL_W - 12 - settingsX();
	}

	private int searchY() {
		return panelY() + 34;
	}

	private int tabsY() {
		return panelY() + 62;
	}

	private int tabRowWidth() {
		return SIDEBAR_W - 16;
	}

	private List<Category> tabs() {
		List<Category> tabs = new ArrayList<>();
		tabs.add(null);
		tabs.add(Category.HUD);
		tabs.add(Category.RENDER);
		tabs.add(Category.WORLD);
		tabs.add(Category.COMBAT);
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

		double t = open.get();
		int dim = (int) Math.round(0x96 * t);
		context.fill(0, 0, this.width, this.height, dim << 24);

		int x = panelX();
		int y = panelY();

		var matrices = context.getMatrices();
		matrices.push();

		float scale = (float) (0.97 + 0.03 * t);
		matrices.translate(x + PANEL_W / 2.0f, y + PANEL_H / 2.0f, 0.0f);
		matrices.scale(scale, scale, 1.0f);
		matrices.translate(-(x + PANEL_W / 2.0f), -(y + PANEL_H / 2.0f), 0.0f);

		RenderUtils.roundedRect(context, x - 5, y - 5, x + PANEL_W + 5, y + PANEL_H + 5, 14, 0x55000000);
		RenderUtils.roundedRect(context, x, y, x + PANEL_W, y + PANEL_H, 10, PANEL_COLOR);
		RenderUtils.gradientRect(context, x + 1, y + 1, x + PANEL_W - 1, y + 44, 0xFF151B28, PANEL_COLOR);
		RenderUtils.roundedRect(context, x, y, x + PANEL_W, y + 3, 3, ACCENT);
		RenderUtils.outline(context, x, y, x + PANEL_W, y + PANEL_H, BORDER_COLOR);
		RenderUtils.roundedRect(context, sidebarX(), y + 4, sidebarX() + SIDEBAR_W, y + PANEL_H - 4, 8, SIDEBAR_COLOR);

		TextRenderer renderer = this.textRenderer;
		RenderUtils.text(context, renderer, "Nevva", x + 14, y + 13, TEXT_COLOR);
		RenderUtils.text(context, renderer, "Visuals", x + 14 + renderer.getWidth("Nevva") + 2, y + 13, ACCENT);
		RenderUtils.text(context, renderer, "v1.1", x + 14 + renderer.getWidth("NevvaVisuals") + 8, y + 13, TEXT_DIM);

		String hint = "Right Shift";
		RenderUtils.text(context, renderer, hint, x + PANEL_W - 14 - renderer.getWidth(hint), y + 13, TEXT_DIM);

		drawSearch(context, renderer);
		drawTabs(context, renderer, mouseX, mouseY);
		drawList(context, renderer, mouseX, mouseY);
		drawSettings(context, renderer);
		drawFooter(context, renderer);

		matrices.pop();
	}

	private void drawSearch(DrawContext context, TextRenderer renderer) {
		int sx = sidebarX() + 10;
		int sy = searchY();
		int sw = SIDEBAR_W - 20;

		RenderUtils.roundedRect(context, sx, sy, sx + sw, sy + 18, 5, COLUMN_COLOR);
		RenderUtils.outline(context, sx, sy, sx + sw, sy + 18, BORDER_COLOR);

		String text = search.isEmpty() ? "Search..." : search;
		int color = search.isEmpty() ? TEXT_DIM : TEXT_COLOR;
		RenderUtils.text(context, renderer, text, sx + 7, sy + 5, color);

		if (!search.isEmpty() && (System.currentTimeMillis() / 500L) % 2L == 0L) {
			int caretX = sx + 7 + renderer.getWidth(search);
			context.fill(caretX + 1, sy + 4, caretX + 2, sy + 14, ACCENT_2);
		}
	}

	private void drawTabs(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
		List<Category> tabs = tabs();
		int x = sidebarX() + 8;
		int width = tabRowWidth();

		for (int i = 0; i < tabs.size(); i++) {
			Category tab = tabs.get(i);
			int ty = tabsY() + i * 22;
			boolean active = category == tab;
			boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= ty && mouseY <= ty + 19;

			if (active) {
				RenderUtils.roundedRect(context, x, ty, x + width, ty + 19, 5, 0xFF1B2230);
				RenderUtils.roundedRect(context, x, ty + 5, x + 2, ty + 14, 1, ACCENT);
			} else if (hovered) {
				RenderUtils.roundedRect(context, x, ty, x + width, ty + 19, 5, 0xFF141A24);
			}

			String label = tab == null ? "All modules" : tab.getLabel();
			RenderUtils.text(context, renderer, label, x + 10, ty + 6, active ? TEXT_COLOR : TEXT_DIM);
		}
	}

	private void drawList(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
		List<Module> modules = visibleModules();
		int maxScroll = Math.max(0, modules.size() * ROW_H - listH());
		scroll = (int) MathHelper.clamp(scroll, 0, maxScroll);

		RenderUtils.roundedRect(context, listX(), listY(), listX() + LIST_W, listY() + listH(), 7, COLUMN_COLOR);
		RenderUtils.outline(context, listX(), listY(), listX() + LIST_W, listY() + listH(), BORDER_COLOR);

		context.enableScissor(listX() + 1, listY() + 1, listX() + LIST_W - 1, listY() + listH() - 1);

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			int ry = listY() + 3 + i * ROW_H - scroll;

			if (ry + ROW_H < listY() || ry > listY() + listH()) {
				continue;
			}

			Animation rowHover = hover.computeIfAbsent(module, k -> new Animation(0.0, 14.0));
			boolean hovered = mouseX >= listX() && mouseX <= listX() + LIST_W && mouseY >= ry && mouseY <= ry + ROW_H - 3;
			rowHover.setTarget(hovered ? 1.0 : 0.0);
			rowHover.update();

			if (rowHover.get() > 0.01) {
				RenderUtils.roundedRect(context, listX() + 3, ry, listX() + LIST_W - 3, ry + ROW_H - 4, 5,
						RenderUtils.withAlpha(ACCENT, rowHover.get() * 0.20));
			}

			if (module == selected) {
				RenderUtils.roundedRect(context, listX() + 3, ry, listX() + LIST_W - 3, ry + ROW_H - 4, 5, 0x1FFFFFFF);
				RenderUtils.roundedRect(context, listX() + 3, ry + 5, listX() + 5, ry + ROW_H - 9, 1, ACCENT_2);
			}

			RenderUtils.text(context, renderer, module.getName(), listX() + 14, ry + 5, TEXT_COLOR);
			RenderUtils.text(context, renderer, module.getCategory().getLabel(), listX() + 14, ry + 15, RenderUtils.withAlpha(TEXT_DIM, 0.85));
			drawModuleKnob(context, module, listX() + LIST_W - 37, ry + 8);
		}

		context.disableScissor();

		if (modules.isEmpty()) {
			RenderUtils.centeredText(context, renderer, "Nothing found", listX() + LIST_W / 2, listY() + 20, TEXT_DIM);
		}
	}

	private void drawModuleKnob(DrawContext context, Module module, int x, int y) {
		Animation animation = knobs.computeIfAbsent(module, k -> new Animation(module.isEnabled() ? 1.0 : 0.0, 16.0));
		animation.setTarget(module.isEnabled() ? 1.0 : 0.0);
		animation.update();

		double v = animation.get();
		RenderUtils.roundedRect(context, x, y, x + 26, y + 11, 5, RenderUtils.lerpColor(TRACK_COLOR, ACCENT, v));
		int knobX = (int) Math.round(x + 2 + v * 13);
		RenderUtils.roundedRect(context, knobX, y + 1, knobX + 9, y + 10, 4, 0xFFF4F6FB);
	}

	private void drawFooter(DrawContext context, TextRenderer renderer) {
		int y = panelY() + PANEL_H - 18;
		RenderUtils.text(context, renderer, "Right Shift to close", panelX() + 14, y, TEXT_DIM);

		String count = mod.getModuleManager().getModules().size() + " modules";
		RenderUtils.text(context, renderer, count, panelX() + PANEL_W - 14 - renderer.getWidth(count), y, TEXT_DIM);
	}

	private void drawSettings(DrawContext context, TextRenderer renderer) {
		hits.clear();

		int sx = settingsX();
		int sw = settingsW();
		int sy = panelY() + 58;
		int sh = listH();

		RenderUtils.roundedRect(context, sx, sy, sx + sw, sy + sh, 7, COLUMN_COLOR);
		RenderUtils.outline(context, sx, sy, sx + sw, sy + sh, BORDER_COLOR);

		if (selected == null) {
			RenderUtils.centeredText(context, renderer, "Select a module", sx + sw / 2, sy + 20, TEXT_DIM);
			return;
		}

		RenderUtils.text(context, renderer, selected.getName(), sx + 12, sy + 10, TEXT_COLOR);
		RenderUtils.text(context, renderer, selected.getCategory().getLabel(), sx + 12, sy + 21, ACCENT_2);
		RenderUtils.text(context, renderer, trim(selected.getDescription(), sw - 24, renderer), sx + 12, sy + 34, TEXT_DIM);

		int y = sy + 52;

		context.enableScissor(sx + 1, sy + 1, sx + sw - 1, sy + sh - 1);

		for (Setting<?> setting : selected.getSettings()) {
			if (setting instanceof BoolSetting bool) {
				RenderUtils.text(context, renderer, bool.getLabel(), sx + 12, y + 2, TEXT_COLOR);
				drawStaticKnob(context, sx + sw - 38, y, bool.get());
				hits.add(new Hit(setting, sx + sw - 40, y - 2, sx + sw - 8, y + 14, 1));
				y += 24;
			} else if (setting instanceof NumberSetting number) {
				RenderUtils.text(context, renderer, number.getLabel(), sx + 12, y, TEXT_COLOR);

				String value = format(number.get());
				RenderUtils.text(context, renderer, value, sx + sw - 12 - renderer.getWidth(value), y, ACCENT_2);

				int barX1 = sx + 12;
				int barX2 = sx + sw - 12;
				int barY = y + 13;
				double progress = MathHelper.clamp(number.getProgress(), 0.0, 1.0);

				RenderUtils.roundedRect(context, barX1, barY, barX2, barY + 4, 2, 0x40232B38);
				int fill = barX1 + (int) Math.round((barX2 - barX1) * progress);
				RenderUtils.roundedRect(context, barX1, barY, Math.max(barX1 + 2, fill), barY + 4, 2, ACCENT);
				hits.add(new Hit(setting, barX1, barY - 5, barX2, barY + 9, 2));
				y += 28;
			} else if (setting instanceof ColorSetting color) {
				RenderUtils.text(context, renderer, color.getLabel(), sx + 12, y, TEXT_COLOR);

				int previewX = sx + sw - 26;
				RenderUtils.roundedRect(context, previewX, y - 1, previewX + 14, y + 9, 3, 0xFF000000 | color.getColor());

				int barX1 = sx + 12;
				int barX2 = sx + sw - 32;
				int barY = y + 13;

				for (int i = 0; i < 24; i++) {
					int segmentX1 = barX1 + (barX2 - barX1) * i / 24;
					int segmentX2 = barX1 + (barX2 - barX1) * (i + 1) / 24 + 1;
					int rgb = ColorSetting.hsvToRgb(i * 15.0f, 0.72f, 1.0f);
					context.fill(segmentX1, barY, segmentX2, barY + 4, 0xFF000000 | rgb);
				}

				int knobX = barX1 + (int) Math.round((barX2 - barX1) * (color.getHue() / 360.0f));
				RenderUtils.roundedRect(context, knobX - 2, barY - 1, knobX + 2, barY + 5, 2, 0xFFFFFFFF);
				hits.add(new Hit(setting, barX1, barY - 5, barX2, barY + 9, 4));
				y += 28;
			} else if (setting instanceof ChoiceSetting choice) {
				RenderUtils.text(context, renderer, choice.getLabel(), sx + 12, y + 2, TEXT_COLOR);

				String value = "< " + choice.getValue() + " >";
				int valueWidth = renderer.getWidth(value);
				int valueX = sx + sw - 12 - valueWidth;
				RenderUtils.text(context, renderer, value, valueX, y + 2, ACCENT_2);
				hits.add(new Hit(setting, valueX - 4, y - 2, sx + sw - 8, y + 14, 3));
				y += 24;
			}
		}

		context.disableScissor();
	}

	private void drawStaticKnob(DrawContext context, int x, int y, boolean on) {
		RenderUtils.roundedRect(context, x, y, x + 26, y + 11, 5, on ? ACCENT : TRACK_COLOR);
		int knobX = on ? x + 15 : x + 2;
		RenderUtils.roundedRect(context, knobX, y + 1, knobX + 9, y + 10, 4, 0xFFF4F6FB);
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
		int mx = (int) mouseX;
		int my = (int) mouseY;

		int tabX = sidebarX() + 8;
		List<Category> tabs = tabs();

		for (int i = 0; i < tabs.size(); i++) {
			int ty = tabsY() + i * 22;

			if (mx >= tabX && mx <= tabX + tabRowWidth() && my >= ty && my <= ty + 19) {
				category = tabs.get(i);
				scroll = 0;
				return true;
			}
		}

		List<Module> modules = visibleModules();

		for (int i = 0; i < modules.size(); i++) {
			int ry = listY() + 3 + i * ROW_H - scroll;

			if (ry < listY() || ry + ROW_H - 3 > listY() + listH()) {
				continue;
			}

			if (mx >= listX() && mx <= listX() + LIST_W && my >= ry && my <= ry + ROW_H - 3) {
				Module module = modules.get(i);
				selected = module;
				module.toggle();
				return true;
			}
		}

		for (Hit hit : hits) {
			if (mx >= hit.x1 && mx <= hit.x2 && my >= hit.y1 && my <= hit.y2) {
				applyHit(hit, mx);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void applyHit(Hit hit, int mouseX) {
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
					dragSetting.setProgress((mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1));
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
		if (mouseX >= listX() && mouseX <= listX() + LIST_W && mouseY >= listY() && mouseY <= listY() + listH()) {
			List<Module> modules = visibleModules();
			int maxScroll = Math.max(0, modules.size() * ROW_H - listH());
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
}
