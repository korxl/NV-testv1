package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.render.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Slide-in notifications shown when a module is toggled.
 */
public final class Notifications {
	private static final long LIFETIME = 2200L;
	private static final int MAX_ENTRIES = 4;

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

		TextRenderer renderer = client.textRenderer;
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();
		int y = screenHeight - 26;

		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry entry = entries.get(i);
			double alpha = entry.animation.get();

			if (alpha <= 0.02) {
				continue;
			}

			String state = entry.enabled ? "ON" : "OFF";
			int width = 10 + renderer.getWidth(entry.text) + 8 + renderer.getWidth(state) + 10;
			int x = screenWidth - (int) Math.round(width * alpha) - 6;
			int alphaByte = (int) Math.round(235 * alpha);

			RenderUtils.roundedRect(context, x, y, x + width, y + 16, 5, (alphaByte << 24) | 0x0B0E14);
			RenderUtils.roundedRect(context, x, y, x + 2, y + 16, 1, (int) Math.round(255 * alpha) << 24 | 0x7C5CFF);
			RenderUtils.text(context, renderer, entry.text, x + 10, y + 4, ((int) Math.round(255 * alpha) << 24) | 0xE6EAF2);
			RenderUtils.text(context, renderer, state, x + width - 10 - renderer.getWidth(state), y + 4,
					((int) Math.round(255 * alpha) << 24) | (entry.enabled ? 0x9BE38B : 0xFF8A8A));

			y -= 20;
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
