package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.modules.TargetHudModule;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Locale;

/**
 * Shows the last player that was hit, for three seconds after the last attack.
 */
public final class TargetHud {
	private static final long VISIBLE_MILLIS = 3000L;

	private final NevvaVisuals mod;
	private final Animation appear = new Animation(0.0, 14.0);
	private final Animation healthBar = new Animation(1.0, 9.0);

	private PlayerEntity target;
	private String name = "";
	private long lastHit;

	public TargetHud(NevvaVisuals mod) {
		this.mod = mod;
	}

	public void tick(MinecraftClient client) {
		TargetHudModule module = mod.getModuleManager() == null ? null : mod.getModuleManager().get(TargetHudModule.class);

		if (module == null || !module.isEnabled() || client.player == null || client.options == null) {
			target = null;
			appear.setTarget(0.0);
			appear.update();
			return;
		}

		if (client.options.attackKey.isPressed() && client.crosshairTarget instanceof EntityHitResult hit) {
			if (hit.getEntity() instanceof PlayerEntity player && player != client.player && player.isAlive()) {
				target = player;
				name = player.getName().getString();
				lastHit = System.currentTimeMillis();
			}
		}

		if (target != null && (System.currentTimeMillis() - lastHit > VISIBLE_MILLIS || target.isRemoved() || !target.isAlive())) {
			target = null;
		}

		appear.setTarget(target != null ? 1.0 : 0.0);
		appear.update();

		if (target != null) {
			float max = Math.max(1.0f, target.getMaxHealth());
			healthBar.setTarget(Math.max(0.0f, Math.min(1.0f, target.getHealth() / max)));
		}

		healthBar.update();
	}

	public void render(DrawContext context) {
		if (target == null || appear.get() <= 0.02) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden || mod.getModuleManager() == null) {
			return;
		}

		TargetHudModule module = mod.getModuleManager().get(TargetHudModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		Theme theme = mod.getTheme();
		double t = appear.get();
		double userScale = module.getScale().get() / 100.0;
		int width = 152;
		int height = module.getHealthBar().get() ? 44 : 34;
		int centerX = client.getWindow().getScaledWidth() / 2;
		int y = client.getWindow().getScaledHeight() / 2 + 42;
		int x = centerX - width / 2;
		float health = (float) Math.max(0.0, Math.min(1.0, healthBar.get()));
		TextRenderer renderer = client.textRenderer;

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(centerX, y + height / 2.0f, 0.0f);
		float scale = (float) ((0.88 + 0.12 * t) * userScale);
		matrices.scale(scale, scale, 1.0f);
		matrices.translate(-centerX, -(y + height / 2.0f), 0.0f);

		RenderUtils.roundedRect(context, x, y, x + width, y + height, 7, RenderUtils.scaled(theme.card, t));
		RenderUtils.roundedRect(context, x, y, x + 2, y + height, 2, RenderUtils.withAlpha(theme.accent, t));
		RenderUtils.outline(context, x, y, x + width, y + height, RenderUtils.scaled(theme.border, t));

		RenderUtils.text(context, renderer, name, x + 10, y + 7, RenderUtils.withAlpha(theme.text, t));

		String healthText = String.format(Locale.ROOT, "%.1f", target.getHealth());
		RenderUtils.text(context, renderer, healthText, x + width - 10 - RenderUtils.width(renderer, healthText), y + 7,
				RenderUtils.withAlpha(theme.accent2, t));

		if (module.getDistance().get()) {
			float distance = client.player.distanceTo(target);
			RenderUtils.textNoShadow(context, renderer, String.format(Locale.ROOT, "%.1fm", distance), x + 10, y + 19,
					RenderUtils.withAlpha(theme.dim, t));
		}

		if (module.getHealthBar().get()) {
			int barY = y + 33;
			RenderUtils.roundedRect(context, x + 10, barY, x + width - 10, barY + 4, 2, RenderUtils.scaled(theme.track, t));
			RenderUtils.horizontalGradient(context, x + 10, barY, x + 10 + (int) ((width - 20) * health), barY + 4,
					RenderUtils.withAlpha(theme.accent, t), RenderUtils.withAlpha(theme.accent2, t));
		}

		matrices.pop();
	}
}
