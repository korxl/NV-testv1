package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.WatermarkModule;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Animated logo watermark with the player name and version.
 */
public final class Watermark {
	private static final String NAME = "NevvaVisuals";
	private static final String VERSION = "1.2";

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

		Theme theme = mod.getTheme();
		float scale = (float) (module.getScale().get() / 100.0);
		TextRenderer renderer = client.textRenderer;
		long time = System.currentTimeMillis();
		String nick = client.player.getGameProfile().getName();

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(6.0f, 4.0f, 0.0f);
		matrices.scale(scale, scale, 1.0f);

		int x = 0;

		for (int i = 0; i < NAME.length(); i++) {
			double progress = NAME.length() <= 1 ? 0.0 : (double) i / (NAME.length() - 1);
			double wave = 0.5 + 0.5 * Math.sin(time / 420.0 + i * 0.32);
			int color = RenderUtils.lerpColor(theme.accent, theme.accent2, (progress * 0.7 + wave * 0.3) % 1.0);
			String character = String.valueOf(NAME.charAt(i));

			RenderUtils.text(context, renderer, character, x, 0, color);
			x += renderer.getWidth(character);
		}

		String tail = " " + VERSION + " | " + nick;
		RenderUtils.text(context, renderer, tail, x + 4, 0, theme.dim);

		matrices.pop();
	}
}
