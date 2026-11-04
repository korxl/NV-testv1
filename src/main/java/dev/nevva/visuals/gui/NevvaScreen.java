package dev.nevva.visuals.gui;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
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
 * The NevvaVisuals menu: category tabs on the left, the module list in the middle
 * and the settings of the selected module on the right.
 */
public class NevvaScreen extends Screen {
	private static final int PANEL_W = 560;
	private static final int PANEL_H = 320;

	private static final int TAB_W = 104;
	private static final int LIST_W = 176;
	private static final int ROW_H = 24;

	private static final int PANEL_COLOR = 0xF00B0F16;
	private static final int SIDE_COLOR = 0xFF0F141D;
	private static final int BORDER_COLOR = 0xFF2A3444;
	private static final int TEXT_COLOR = 0xFFE6EAF2;
	private static final int TEXT_DIM = 0xFF8A93A6;
	private static final int ACCENT = 0xFF7C5CFF;
	private static final int ACCENT_2 = 0xFF35D6FF;
	private static final int TRACK_COLOR = 0xFF2A3242;

	private final NevvaVisuals mod;
	private final Animation open = new Animation(0.0, 12.0);
	private final Map<Module, Animation> hover = new HashMap<>();
	private final Map<Module, Animation> knobs = new HashMap<>();
	private final List<Hit> hits = new ArrayList<>();

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

		if (selected == null && !mod.getModuleManager().getModules().isEmpty()) {
			selected = mod.getModuleManager().getModules().get(0);
		}
	}

	private int px() {
		return (this.width - PANEL_W) / 2;
	}

	private int py() {
		return (this.height - PANEL_H) / 2;
	}

	private int tabsX() {
		return panelX() + 12;
	}

	private int tabsY() {
		return panelY() + 46;
	}

	private int listX() {
		return panelX() + 12 + TAB_W + 10;
	}

	private int listY() {
		return panelY() + 46;
	}

	private int listH() {
		return PANEL_H - 46 - 12;
	}

	private int settingsX() {
		return listX() + LIST_W + 10;
	}

	private int settingsY() {
		return panelY() + 46;
	}

	private int settingsW() {
		return panelX() + PANEL_W - 12 - settingsX();
	}

	private int panelX() {
		return px();
	}

	private int panelY() {
		return py();
	}

	private List<Category> tabs() {
		List<Category> tabs = new ArrayList<>();
		tabs.add(null);
		tabs.add(Category.HUD);
		tabs.add(Category.RENDER);
		tabs.add(Category.WORLD);
		tabs.add(Category.PLAYER);

		return tabs;
	}

	private List<Module> visibleModules() {
		List<Module> result = new ArrayList<>();

		for (Module module : mod.getModuleManager().getModules()) {
			if (category == null || module.getCategory() == category) {
				result.add(module);
			}
		}

		return result;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		open.setTarget(1.0);
		open.update();

		double t = open.get();
		int dim = (int) Math.round(0x8C * t);
		context.fill(0, 0, this.width, this.height, dim << 24);

		int x = panelX();
		int y = panelY();

		var matrices = context.getMatrices();
		matrices.push();

		float scale = (float) (0.97 + 0.03 * t);
		matrices.translate(x + PANEL_W / 2.0f, y + PANEL_H / 2.0f, 0.0f);
		matrices.scale(scale, scale, 1.0f);
		matrices.translate(-(x + PANEL_W / 2.0f), -(y + PANEL_H / 2.0f), 0.0f);

		RenderUtils.roundedRect(context, x - 4, y - 4, x + PANEL_W + 4, y + PANEL_H + 4, 12, 0x50000000);
		RenderUtils.roundedRect(context, x, y, x + PANEL_W, y + PANEL_H, 10, PANEL_COLOR);
		RenderUtils.gradientRect(context, x + 1, y + 1, x + PANEL_W - 1, y + 42, 0xFF141A26, PANEL_COLOR);
		RenderUtils.roundedRect(context, x, y, x + PANEL_W, y + 3, 3, ACCENT);
		RenderUtils.outline(context, x, y, x + PANEL_W, y + PANEL_H, BORDER_COLOR);

		TextRenderer renderer = this.textRenderer;
		RenderUtils.text(context, renderer, "Nevva", x + 16, y + 14, TEXT_COLOR);
		RenderUtils.text(context, renderer, "Visuals", x + 16 + renderer.getWidth("Nevva") + 1, y + 14, ACCENT);
		RenderUtils.text(context, renderer, "v1.0", x + 16 + renderer.getWidth("NevvaVisuals") + 8, y + 14, TEXT_DIM);

		String hint = "Right Shift";
		RenderUtils.text(context, renderer, hint, x + PANEL_W - 16 - renderer.getWidth(hint), y + 14, TEXT_DIM);

		drawTabs(context, renderer, mouseX, mouseY);
		drawList(context, renderer, mouseX, mouseY);
		drawSettings(context, renderer, mouseX, mouseY);

		matrices.pop();
	}

	private void drawTabs(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
		List<Category> tabs = tabs();

		for (int i = 0; i < tabs.size(); i++) {
			Category tab = tabs.get(i);
			int ty = tabsY() + i * 24;
			boolean active = category == tab;
			boolean hovered = mouseX >= tabsX() && mouseX <= tabsX() + TAB_W && mouseY >= ty && mouseY <= ty + 20;

			if (active) {
				RenderUtils.roundedRect(context, tabsX(), ty, tabsX() + TAB_W, ty + 20, 5, 0xFF1B2230);
				RenderUtils.roundedRect(context, tabsX(), ty + 6, tabsX() + 2, ty + 14, 1, ACCENT);
			} else if (hovered) {
				RenderUtils.roundedRect(context, tabsX(), ty, tabsX() + TAB_W, ty + 20, 5, 0xFF151B26);
			}

			String label = tab == null ? "All" : tab.getLabel();
			RenderUtils.text(context, renderer, label, tabsX() + 10, ty + 7, active ? TEXT_COLOR : TEXT_DIM);
		}
	}

	private void drawList(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
		List<Module> modules = visibleModules();
		int maxScroll = Math.max(0, modules.size() * ROW_H - listH());
		scroll = (int) MathHelper.clamp(scroll, 0, maxScroll);

		RenderUtils.roundedRect(context, listX(), listY(), listX() + LIST_W, listY() + listH(), 6, SIDE_COLOR);
		RenderUtils.outline(context, listX(), listY(), listX() + LIST_W, listY() + listH(), 0xFF1A2230);

		context.enableScissor(listX() + 1, listY() + 1, listX() + LIST_W - 1, listY() + listH() - 1);

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			int ry = listY() + 2 + i * ROW_H - scroll;

			if (ry + ROW_H < listY() || ry > listY() + listH()) {
				continue;
			}

			Animation rowHover = hover.computeIfAbsent(module, k -> new Animation(0.0, 14.0));
			boolean hovered = mouseX >= listX() && mouseX <= listX() + LIST_W && mouseY >= ry && mouseY <= ry + ROW_H - 2;
			rowHover.setTarget(hovered ? 1.0 : 0.0);
			rowHover.update();

			if (rowHover.get() > 0.01) {
				RenderUtils.roundedRect(context, listX() + 3, ry, listX() + LIST_W - 3, ry + ROW_H - 3, 5,
						RenderUtils.withAlpha(ACCENT, rowHover.get() * 0.22));
			}

			if (module == selected) {
				RenderUtils.roundedRect(context, listX() + 3, ry, listX() + LIST_W - 3, ry + ROW_H - 3, 5, 0x22FFFFFF);
			}

			RenderUtils.text(context, renderer, module.getName(), listX() + 10, ry + 4, TEXT_COLOR);
			RenderUtils.text(context, renderer, module.getCategory().getLabel(), listX() + 10, ry + 13, RenderUtils.withAlpha(TEXT_DIM, 0.8));
			drawModuleKnob(context, module, listX() + LIST_W - 34, ry + 6);
		}

		context.disableScissor();
	}

	private void drawModuleKnob(DrawContext context, Module module, int x, int y) {
		Animation animation = knobs.computeIfAbsent(module, k -> new Animation(module.isEnabled() ? 1.0 : 0.0, 16.0));
		animation.setTarget(module.isEnabled() ? 1.0 : 0.0);
		animation.update();

		double v = animation.get();
		RenderUtils.roundedRect(context, x, y, x + 24, y + 10, 5, RenderUtils.lerpColor(TRACK_COLOR, ACCENT, v));
		int knobX = (int) Math.round(x + 2 + v * 12);
		RenderUtils.roundedRect(context, knobX, y + 1, knobX + 8, y + 9, 4, 0xFFF2F5FA);
	}

	private void drawStaticKnob(DrawContext context, int x, int y, boolean on) {
		RenderUtils.roundedRect(context, x, y, x + 24, y + 10, 5, on ? ACCENT : TRACK_COLOR);
		int knobX = on ? x + 14 : x + 2;
		RenderUtils.roundedRect(context, knobX, y + 1, knobX + 8, y + 9, 4, 0xFFF2F5FA);
	}

	private void drawSettings(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
		hits.clear();

		int sx = settingsX();
		int sw = settingsW();

		RenderUtils.roundedRect(context, sx, settingsY(), sx + sw, settingsY() + listH(), 6, SIDE_COLOR);
		RenderUtils.outline(context, sx, settingsY(), sx + sw, settingsY() + listH(), 0xFF1A2230);

		if (selected == null) {
			RenderUtils.centeredText(context, renderer, "Select a module", sx + sw / 2, settingsY() + 16, TEXT_DIM);
			return;
		}

		context.enableScissor(sx + 1, settingsY() + 1, sx + sw - 1, settingsY() + listH() - 1);

		RenderUtils.text(context, renderer, selected.getName(), sx + 10, settingsY() + 8, TEXT_COLOR);
		RenderUtils.text(context, renderer, trim(selected.getDescription(), sw - 20, renderer), sx + 10, settingsY() + 19, TEXT_DIM);

		int y = settingsY() + 36;

		if (selected.getSettings().isEmpty()) {
			RenderUtils.text(context, renderer, "No settings", sx + 10, y, TEXT_DIM);
			context.disableScissor();
			return;
		}

		for (Setting<?> setting : selected.getSettings()) {
			if (setting instanceof BoolSetting bool) {
				RenderUtils.text(context, renderer, bool.getLabel(), sx + 10, y + 2, TEXT_COLOR);
				drawStaticKnob(context, sx + sw - 34, y, bool.get());
				hits.add(new Hit(setting, sx + sw - 36, y - 2, sx + sw - 8, y + 14, 1));

				y += 24;
			} else if (setting instanceof NumberSetting number) {
				RenderUtils.text(context, renderer, number.getLabel(), sx + 10, y, TEXT_COLOR);
				String value = format(number.get());
				RenderUtils.text(context, renderer, value, sx + sw - 10 - renderer.getWidth(value), y, ACCENT_2);

				int barX1 = sx + 10;
				int barX2 = sx + sw - 10;
				int barY = y + 13;

				RenderUtils.roundedRect(context, barX1, barY, barX2, barY + 4, 2, 0x402A3242);
				int fill = barX1 + (int) Math.round((barX2 - barX1) * MathHelper.clamp(number.getProgress(), 0.0, 1.0));
				RenderUtils.roundedRect(context, barX1, barY, Math.max(barX1 + 2, fill), barY + 4, 2, ACCENT);
				hits.add(new Hit(setting, barX1, barY - 5, barX2, barY + 9, 2));

				y += 28;
			} else if (setting instanceof ChoiceSetting choice) {
				RenderUtils.text(context, renderer, choice.getLabel(), sx + 10, y + 2, TEXT_COLOR);

				String value = "< " + choice.getValue() + " >";
				int valueWidth = renderer.getWidth(value);
				int valueX = sx + sw - 10 - valueWidth;
				RenderUtils.text(context, renderer, value, valueX, y + 2, ACCENT_2);
				hits.add(new Hit(setting, valueX - 4, y - 2, sx + sw - 6, y + 14, 3));

				y += 24;
			}
		}

		context.disableScissor();
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
				: String.format(Locale.ROOT, "%.2f", value);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int mx = (int) mouseX;
		int my = (int) mouseY;

		List<Category> tabs = tabs();

		for (int i = 0; i < tabs.size(); i++) {
			int ty = tabsY() + i * 24;

			if (mx >= tabsX() && mx <= tabsX() + TAB_W && my >= ty && my <= ty + 20) {
				category = tabs.get(i);
				scroll = 0;
				return true;
			}
		}

		List<Module> modules = visibleModules();

		for (int i = 0; i < modules.size(); i++) {
			int ry = listY() + 2 + i * ROW_H - scroll;

			if (ry < listY() || ry + ROW_H - 2 > listY() + listH()) {
				continue;
			}

			if (mx >= listX() && mx <= listX() + LIST_W && my >= ry && my <= ry + ROW_H - 2) {
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
			double progress = (mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1);
			number.setProgress(progress);
			mod.saveConfig();
		} else if (hit.type == 3 && hit.setting instanceof ChoiceSetting choice) {
			choice.cycle(1);
			mod.saveConfig();
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (dragging && dragSetting != null) {
			for (Hit hit : hits) {
				if (hit.setting == dragSetting && hit.type == 2) {
					double progress = (mouseX - hit.x1) / (double) Math.max(1, hit.x2 - hit.x1);
					dragSetting.setProgress(progress);
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
