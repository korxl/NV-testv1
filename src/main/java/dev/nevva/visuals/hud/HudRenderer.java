package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.HudModule;
import dev.nevva.visuals.render.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The configurable info overlay. No module list here: everything that is enabled
 * is visible in the menu only.
 */
public final class HudRenderer {
	private static final int LINE_HEIGHT = 10;
	private static final int BACKGROUND = 0x8C0B0E14;
	private static final int TEXT_COLOR = 0xFFE6EAF2;
	private static final int VALUE_COLOR = 0xFF35D6FF;

	private final NevvaVisuals mod;

	private long lastTickNanos;
	private double tickMillis = 50.0;
	private double lastX;
	private double lastY;
	private double lastZ;
	private boolean hasLastPosition;
	private double speed;

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

		if (client.player == null) {
			hasLastPosition = false;
			speed = 0.0;
			return;
		}

		double x = client.player.getX();
		double y = client.player.getY();
		double z = client.player.getZ();

		if (hasLastPosition) {
			double dx = x - lastX;
			double dy = y - lastY;
			double dz = z - lastZ;
			speed = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
		}

		lastX = x;
		lastY = y;
		lastZ = z;
		hasLastPosition = true;
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.world == null || client.options == null) {
			return;
		}

		if (client.options.hudHidden || mod.getModuleManager() == null) {
			return;
		}

		HudModule module = mod.getModuleManager().get(HudModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		TextRenderer textRenderer = client.textRenderer;
		List<String[]> lines = buildLines(client, module);

		if (lines.isEmpty()) {
			return;
		}

		boolean background = module.getBackground().get();
		int screenWidth = client.getWindow().getScaledWidth();
		int x = 6;
		int y = mod.getModuleManager().get(dev.nevva.visuals.module.modules.WatermarkModule.class) != null
				&& mod.getModuleManager().get(dev.nevva.visuals.module.modules.WatermarkModule.class).isEnabled()
				? 20 : 6;

		if (module.isRight()) {
			x = screenWidth - 6 - width(textRenderer, lines);
		}

		for (String[] line : lines) {
			int lineWidth = textRenderer.getWidth(line[0] + line[1]);

			if (background) {
				RenderUtils.roundedRect(context, x - 3, y - 1, x + lineWidth + 3, y + LINE_HEIGHT - 1, 3, BACKGROUND);
			}

			RenderUtils.text(context, textRenderer, line[0], x, y, TEXT_COLOR);
			RenderUtils.text(context, textRenderer, line[1], x + textRenderer.getWidth(line[0]), y, VALUE_COLOR);
			y += LINE_HEIGHT;
		}
	}

	private int width(TextRenderer renderer, List<String[]> lines) {
		int max = 0;

		for (String[] line : lines) {
			max = Math.max(max, renderer.getWidth(line[0] + line[1]));
		}

		return max;
	}

	private List<String[]> buildLines(MinecraftClient client, HudModule module) {
		List<String[]> lines = new ArrayList<>();

		if (module.getFps().get()) {
			lines.add(new String[]{"FPS: ", String.valueOf(client.getCurrentFps())});
		}

		BlockPos pos = client.player.getBlockPos();

		if (module.getCoords().get()) {
			lines.add(new String[]{"XYZ: ", pos.getX() + " " + pos.getY() + " " + pos.getZ()});
		}

		if (module.getDirection().get()) {
			lines.add(new String[]{"Dir: ", client.player.getHorizontalFacing().asString()});
		}

		if (module.getSpeed().get()) {
			lines.add(new String[]{"Speed: ", String.format(Locale.ROOT, "%.1f", speed) + " b/s"});
		}

		int ping = ping(client);

		if (module.getPing().get() && ping >= 0) {
			lines.add(new String[]{"Ping: ", ping + " ms"});
		}

		if (module.getTps().get()) {
			lines.add(new String[]{"TPS: ", String.format(Locale.ROOT, "%.1f", tps())});
		}

		if (module.getMemory().get()) {
			lines.add(new String[]{"RAM: ", usedMemory() + " MB"});
		}

		return lines;
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
