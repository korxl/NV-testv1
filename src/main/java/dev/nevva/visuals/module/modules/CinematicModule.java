package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;
import dev.nevva.visuals.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;

/**
 * Recording overlay: cinematic letterbox bars, a soft vignette and an optional
 * "hide my HUD" switch. It only draws on top of the finished frame, so the footage
 * stays exactly what the server sees.
 */
public class CinematicModule extends Module {
	private final BoolSetting letterbox = add(new BoolSetting("cinematic_letterbox", "Letterbox bars", true));
	private final NumberSetting barHeight = add(new NumberSetting("cinematic_bar_height", "Bar height %", 8.0, 2.0, 25.0, 1.0));
	private final BoolSetting vignette = add(new BoolSetting("cinematic_vignette", "Vignette", true));
	private final NumberSetting vignetteStrength = add(new NumberSetting("cinematic_vignette_strength", "Vignette strength %", 35.0, 5.0, 90.0, 5.0));
	private final BoolSetting hideHud = add(new BoolSetting("cinematic_hide_hud", "Hide own HUD", false));

	public CinematicModule() {
		super("Cinematic", "Letterbox and vignette overlay for clean recordings", Category.RENDER);
	}

	public BoolSetting getLetterbox() {
		return letterbox;
	}

	public NumberSetting getBarHeight() {
		return barHeight;
	}

	public BoolSetting getVignette() {
		return vignette;
	}

	public NumberSetting getVignetteStrength() {
		return vignetteStrength;
	}

	public BoolSetting getHideHud() {
		return hideHud;
	}

	/** True when the player asked the mod's own HUD to stay out of the frame. */
	public boolean hidesHud() {
		return hideHud.get();
	}

	/** Draws the overlay on top of the HUD pass. */
	public void renderOverlay(DrawContext context) {
		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();

		if (width <= 1 || height <= 1) {
			return;
		}

		if (letterbox.get()) {
			int bar = (int) Math.round(height * barHeight.get() / 100.0);
			bar = Math.max(1, Math.min(height / 3, bar));

			context.fill(0, 0, width, bar, 0xFF000000);
			context.fill(0, height - bar, width, height, 0xFF000000);
		}

		if (vignette.get()) {
			int edge = RenderUtils.withAlpha(0x000000, vignetteStrength.get() / 100.0);
			int depthY = Math.max(12, (int) Math.round(height * 0.26));
			int depthX = Math.max(12, (int) Math.round(width * 0.22));

			int bottomY = Math.max(height / 2, height - depthY);
			int rightX = Math.max(width / 2, width - depthX);

			RenderUtils.gradientRect(context, 0, 0, width, Math.min(height / 2, depthY), edge, 0x00000000);
			RenderUtils.gradientRect(context, 0, bottomY, width, height, 0x00000000, edge);
			RenderUtils.horizontalGradient(context, 0, 0, Math.min(width / 2, depthX), height, edge, 0x00000000);
			RenderUtils.horizontalGradient(context, rightX, 0, width, height, 0x00000000, edge);
		}
	}
}
