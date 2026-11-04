package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.HudModule;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

/**
 * Draws every enabled info element at its own position. Positions and sizes are
 * edited with the HUD editor.
 */
public final class HudRenderer {
	private static final int LINE_HEIGHT = 10;

	private final NevvaVisuals mod;

	private long lastTickNanos;
	private double tickMillis = 50.0;
	private double lastX;
	private double lastY;
	private double lastZ;
	private boolean hasLastPosition;
	private double speed;

	private long lastSecond = System.currentTimeMillis() / 1000L;
	private int clicksThisSecond;
	private int cps;

	public HudRenderer(NevvaVisuals mod) {
		this.mod = mod;
	}

	public void tick(MinecraftClient client) {
		long now = System.nanoTime();

		if (lastTickNanos != 0L) {
			double deltaMillis = (now - lastTickNanos) / 1_000_000.0;
			tickMillis += (deltaMillis - tickMillis) * 0.05;
		}

		lastTickNanos = now;

		if (client.player == null || client.options == null) {
			hasLastPosition = false;
			speed = 0.0;
			return;
		}

		while (client.options.attackKey.wasPressed()) {
			clicksThisSecond++;
		}

		long second = System.currentTimeMillis() / 1000L;

		if (second != lastSecond) {
			lastSecond = second;
			cps = clicksThisSecond;
			clicksThisSecond = 0;
		}

		if (hasLastPosition) {
			double dx = client.player.getX() - lastX;
			double dy = client.player.getY() - lastY;
			double dz = client.player.getZ() - lastZ;
			speed = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
		}

		lastX = client.player.getX();
		lastY = client.player.getY();
		lastZ = client.player.getZ();
		hasLastPosition = true;
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.world == null || client.options == null || client.options.hudHidden) {
			return;
		}

		if (mod.getModuleManager() == null) {
			return;
		}

		HudModule hud = mod.getModuleManager().get(HudModule.class);

		if (hud == null || !hud.isEnabled()) {
			return;
		}

		Theme theme = mod.getTheme();
		TextRenderer renderer = client.textRenderer;
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		for (HudElement element : mod.getHudLayout().elements()) {
			if (!element.enabled) {
				continue;
			}

			String line = element.label + ": " + value(client, element);
			int x = (int) Math.round(element.x * screenWidth);
			int y = (int) Math.round(element.y * screenHeight);

			var matrices = context.getMatrices();
			matrices.push();
			matrices.translate(x, y, 0.0f);
			matrices.scale((float) element.scale, (float) element.scale, 1.0f);

			int width = RenderUtils.width(renderer, line);

			if (hud.getBackground().get()) {
				RenderUtils.roundedRect(context, -3, -1, width + 3, LINE_HEIGHT, 3, theme.card);
			}

			RenderUtils.text(context, renderer, line, 0, 0, theme.text);
			matrices.pop();

			element.lastX = x;
			element.lastY = y;
			element.lastWidth = Math.max(1, (int) Math.round(width * element.scale));
			element.lastHeight = Math.max(1, (int) Math.round(LINE_HEIGHT * element.scale));
		}
	}

	private String value(MinecraftClient client, HudElement element) {
		switch (element.id) {
			case "fps":
				return String.valueOf(client.getCurrentFps());
			case "ping": {
				int ping = ping(client);
				return ping >= 0 ? ping + " ms" : "--";
			}
			case "coords": {
				BlockPos pos = client.player.getBlockPos();
				return pos.getX() + " " + pos.getY() + " " + pos.getZ();
			}
			case "direction": {
				int yaw = Math.round(MathHelper.wrapDegrees(client.player.getYaw()));
				return client.player.getHorizontalFacing().asString() + " " + yaw + "\u00B0";
			}
			case "speed":
				return String.format(Locale.ROOT, "%.2f b/s", speed);
			case "cps":
				return String.valueOf(cps);
			case "armor":
				return String.valueOf(client.player.getArmor());
			case "effects":
				return String.valueOf(client.player.getStatusEffects().size());
			case "tps":
				return String.format(Locale.ROOT, "%.1f", tps());
			case "memory":
				return usedMemory() + " MB";
			case "time": {
				long timeOfDay = client.world.getTimeOfDay() % 24000L;
				long totalMinutes = (timeOfDay * 1440L) / 24000L + 6L * 60L;
				long hours = (totalMinutes / 60L) % 24L;
				long minutes = totalMinutes % 60L;
				return String.format(Locale.ROOT, "%02d:%02d", hours, minutes);
			}
			default:
				return "";
		}
	}

	private int ping(MinecraftClient client) {
		ClientPlayNetworkHandler handler = client.getNetworkHandler();

		if (handler == null || client.player == null) {
			return -1;
		}

		PlayerListEntry entry = handler.getPlayerListEntry(client.player.getUuid());

		return entry == null ? -1 : entry.getLatency();
	}

	private double tps() {
		if (tickMillis <= 0.0) {
			return 20.0;
		}

		return Math.min(20.0, 1000.0 / tickMillis);
	}

	private long usedMemory() {
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
	}
}
