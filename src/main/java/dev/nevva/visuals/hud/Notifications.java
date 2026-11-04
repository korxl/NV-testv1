package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.modules.InterfaceModule;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Slide-in notification cards with an icon dot and a lifetime progress bar.
 */
public final class Notifications {
	private static final long LIFETIME = 2400L;
	private static final int MAX_ENTRIES = 4;
	private static final int WIDTH = 148;

	private final NevvaVisuals mod;
	private final List<Entry> entries = new ArrayList<>();

	public Notifications(NevvaVisuals mod) {
		this.mod = mod;
	}

	public void push(String text, boolean enabled) {
		entries.add(new Entry(text, enabled));

		while (entries.size() > MAX_ENTRIES) {
			entries.remove(0);
		}
	}

	public void tick(MinecraftClient client) {
		long now = System.currentTimeMillis();
		entries.removeIf(entry -> now - entry.createdAt > LIFETIME);

		for (Entry entry : entries) {
			boolean alive = System.currentTimeMillis() - entry.createdAt < LIFETIME - 350;
			entry.animation.setTarget(alive ? 1.0 : 0.0);
			entry.animation.update();
		}
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden || entries.isEmpty()) {
			return;
		}

		InterfaceModule ui = mod.getModuleManager() == null ? null : mod.getModuleManager().get(InterfaceModule.class);

		if (ui != null && !ui.getNotifications().get()) {
			return;
		}

		Theme theme = mod.getTheme();
		TextRenderer renderer = client.textRenderer;
		int screenWidth = client.getWindow().getScaledWidth();
		int y = 6;

		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry entry = entries.get(i);
			double alpha = entry.animation.get();

			if (alpha <= 0.02) {
				continue;
			}

			String state = entry.enabled ? "ENABLED" : "DISABLED";
			int x = screenWidth - (int) Math.round((WIDTH + 6) * alpha) - 6;
			int textColor = RenderUtils.withAlpha(theme.text, alpha);
			int stateColor = RenderUtils.withAlpha(entry.enabled ? theme.accent2 : theme.dim, alpha);
			double life = 1.0 - Math.min(1.0, (System.currentTimeMillis() - entry.createdAt) / (double) LIFETIME);

			RenderUtils.roundedRect(context, x, y, x + WIDTH, y + 30, 5, RenderUtils.scaled(theme.card, alpha));
			RenderUtils.outline(context, x, y, x + WIDTH, y + 30, RenderUtils.scaled(theme.border, alpha));
			RenderUtils.circle(context, x + 11, y + 11, 3.0, RenderUtils.withAlpha(theme.accent, alpha));
			RenderUtils.text(context, renderer, entry.text, x + 20, y + 6, textColor);
			RenderUtils.textNoShadow(context, renderer, state, x + 20, y + 16, stateColor);
			RenderUtils.roundedRect(context, x, y + 28, x + (int) Math.round(WIDTH * life), y + 30, 1,
					RenderUtils.withAlpha(theme.accent, alpha));

			y += 36;
		}
	}

	private static final class Entry {
		final String text;
		final boolean enabled;
		final long createdAt = System.currentTimeMillis();
		final Animation animation = new Animation(0.0, 12.0);

		Entry(String text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}
	}
}
