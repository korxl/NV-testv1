package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Drag and drop editor for the info HUD. Drag with the left mouse button,
 * scroll over an element to resize it, right click toggles it.
 */
public class HudEditorScreen extends Screen {
	private final NevvaVisuals mod;
	private HudElement dragging;
	private double dragOffsetX;
	private double dragOffsetY;

	public HudEditorScreen(NevvaVisuals mod) {
		super(Text.literal("HUD editor"));
		this.mod = mod;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		Theme theme = mod.getTheme();
		context.fill(0, 0, this.width, this.height, 0x99000000);

		for (int x = 0; x < this.width; x += 24) {
			context.fill(x, 0, x + 1, this.height, 0x14FFFFFF);
		}

		for (int y = 0; y < this.height; y += 24) {
			context.fill(0, y, this.width, y + 1, 0x14FFFFFF);
		}

		mod.getHudRenderer().render(context);

		for (HudElement element : mod.getHudLayout().elements()) {
			int color = element.enabled ? theme.accent : theme.dim;
			RenderUtils.outline(context, element.lastX - 3, element.lastY - 2,
					element.lastX + element.lastWidth + 3, element.lastY + element.lastHeight + 1,
					RenderUtils.withAlpha(color, element == dragging ? 0.95 : 0.45));
		}

		RenderUtils.panel(context, 6, this.height - 26, this.width - 6, this.height - 6, 6, theme.card, theme.border);
		RenderUtils.text(context, this.textRenderer,
				"Drag to move  |  Scroll to resize  |  Right click on/off  |  ESC to save",
				14, this.height - 20, theme.text);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (HudElement element : mod.getHudLayout().elements()) {
			if (mouseX >= element.lastX - 3 && mouseX <= element.lastX + element.lastWidth + 3
					&& mouseY >= element.lastY - 2 && mouseY <= element.lastY + element.lastHeight + 1) {
				if (button == 1) {
					element.enabled = !element.enabled;
					mod.saveConfig();
				} else {
					dragging = element;
					dragOffsetX = mouseX - element.lastX;
					dragOffsetY = mouseY - element.lastY;
				}

				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (dragging != null) {
			double width = Math.max(1, this.width);
			double height = Math.max(1, this.height);
			dragging.x = Math.max(0.0, Math.min(1.0, (mouseX - dragOffsetX) / width));
			dragging.y = Math.max(0.0, Math.min(1.0, (mouseY - dragOffsetY) / height));

			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragging != null) {
			dragging = null;
			mod.saveConfig();

			return true;
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		for (HudElement element : mod.getHudLayout().elements()) {
			if (mouseX >= element.lastX - 3 && mouseX <= element.lastX + element.lastWidth + 3
					&& mouseY >= element.lastY - 2 && mouseY <= element.lastY + element.lastHeight + 1) {
				element.scale = Math.max(0.6, Math.min(1.8, element.scale + verticalAmount * 0.05));
				mod.saveConfig();

				return true;
			}
		}

		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public void removed() {
		mod.saveConfig();
		super.removed();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
