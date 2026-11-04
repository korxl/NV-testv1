package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.modules.KeystrokesModule;
import dev.nevva.visuals.render.RenderUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public final class Keystrokes {
	private static final int KEY_SIZE = 16;
	private static final int GAP = 3;

	private final NevvaVisuals mod;
	private final Map<String, Animation> animations = new HashMap<>();

	public Keystrokes(NevvaVisuals mod) {
		this.mod = mod;
	}

	private Animation animation(String key) {
		return animations.computeIfAbsent(key, k -> new Animation(0.0, 18.0));
	}

	public void tick(MinecraftClient client) {
		if (client.options == null) {
			return;
		}

		KeystrokesModule module = mod.getModuleManager() == null ? null : mod.getModuleManager().get(KeystrokesModule.class);
		boolean active = module != null && module.isEnabled();

		update("W", active && client.options.forwardKey.isPressed());
		update("A", active && client.options.leftKey.isPressed());
		update("S", active && client.options.backKey.isPressed());
		update("D", active && client.options.rightKey.isPressed());
		update("LMB", active && client.options.attackKey.isPressed());
		update("RMB", active && client.options.useKey.isPressed());
	}

	private void update(String key, boolean pressed) {
		Animation animation = animation(key);
		animation.setTarget(pressed ? 1.0 : 0.0);
		animation.update();
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden) {
			return;
		}

		KeystrokesModule module = mod.getModuleManager() == null ? null : mod.getModuleManager().get(KeystrokesModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		int x = 6;
		int y = client.getWindow().getScaledHeight() - 74;

		drawKey(context, client, "W", x + KEY_SIZE + GAP, y);
		drawKey(context, client, "A", x, y + KEY_SIZE + GAP);
		drawKey(context, client, "S", x + KEY_SIZE + GAP, y + KEY_SIZE + GAP);
		drawKey(context, client, "D", x + 2 * (KEY_SIZE + GAP), y + KEY_SIZE + GAP);
		drawKey(context, client, "LMB", x, y + 2 * (KEY_SIZE + GAP) - 1);
		drawKey(context, client, "RMB", x + 31, y + 2 * (KEY_SIZE + GAP) - 1);
	}

	private void drawKey(DrawContext context, MinecraftClient client, String label, int x, int y) {
		boolean wide = label.length() > 1;
		int width = wide ? 28 : KEY_SIZE;
		double active = animation(label).get();
		TextRenderer renderer = client.textRenderer;

		RenderUtils.roundedRect(context, x, y, x + width, y + KEY_SIZE, 4, RenderUtils.lerpColor(0x8C0B0E14, 0xE67C5CFF, active));
		RenderUtils.outline(context, x, y, x + width, y + KEY_SIZE, RenderUtils.lerpColor(0x66232B38, 0xFFB9A8FF, active));
		RenderUtils.centeredText(context, renderer, label, x + width / 2, y + 4, RenderUtils.lerpColor(0xFFE6EAF2, 0xFFFFFFFF, active));
	}
}
