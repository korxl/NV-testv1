package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.WatermarkModule;
import dev.nevva.visuals.render.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Animated "NevvaVisuals" logo with a shifting colour gradient.
 */
public final class Watermark {
	private static final String TEXT = "NevvaVisuals";

	private final NevvaVisuals mod;

	public Watermark(NevvaVisuals mod) {
		this.mod = mod;
	}

	public void tick(MinecraftClient client) {
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden || mod.getModuleManager() == null) {
			return;
		}

		WatermarkModule module = mod.getModuleManager().get(WatermarkModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		TextRenderer renderer = client.textRenderer;
		long time = System.currentTimeMillis();
		int x = 6;
		int y = 4;

		RenderUtils.roundedRect(context, x - 3, y - 2, x + renderer.getWidth(TEXT) + 3, y + 10, 3, 0x660B0E14);

		for (int i = 0; i < TEXT.length(); i++) {
			double progress = TEXT.length() <= 1 ? 0.0 : (double) i / (TEXT.length() - 1);
			double wave = 0.5 + 0.5 * Math.sin(time / 420.0 + i * 0.32);
			int color = RenderUtils.lerpColor(0xFF7C5CFF, 0xFF35D6FF, (progress * 0.7 + wave * 0.3) % 1.0);
			String character = String.valueOf(TEXT.charAt(i));

			RenderUtils.text(context, renderer, character, x, y, color);
			x += renderer.getWidth(character);
		}
	}
}
