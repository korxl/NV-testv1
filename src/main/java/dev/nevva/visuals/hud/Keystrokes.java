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
		boolean mouse = module != null && module.getMouse().get();
		boolean space = module != null && module.getSpace().get();

		update("W", active && client.options.forwardKey.isPressed());
		update("A", active && client.options.leftKey.isPressed());
		update("S", active && client.options.backKey.isPressed());
		update("D", active && client.options.rightKey.isPressed());
		update("SPACE", active && space && client.options.jumpKey.isPressed());
		update("LMB", active && mouse && client.options.attackKey.isPressed());
		update("RMB", active && mouse && client.options.useKey.isPressed());
	}

	private void update(String key, boolean pressed) {
		Animation animation = animation(key);
		animation.setTarget(pressed ? 1.0 : 0.0);
		animation.update();
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden || mod.getModuleManager() == null) {
			return;
		}

		KeystrokesModule module = mod.getModuleManager().get(KeystrokesModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		float scale = (float) (module.getScale().get() / 100.0);
		int originX = 6;
		int originY = client.getWindow().getScaledHeight() - 96;

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(originX, originY, 0.0f);
		matrices.scale(scale, scale, 1.0f);

		drawKey(context, client, "W", KEY_SIZE + GAP, 0);
		drawKey(context, client, "A", 0, KEY_SIZE + GAP);
		drawKey(context, client, "S", KEY_SIZE + GAP, KEY_SIZE + GAP);
		drawKey(context, client, "D", 2 * (KEY_SIZE + GAP), KEY_SIZE + GAP);

		int nextRow = 2 * (KEY_SIZE + GAP) + 4;

		if (module.getSpace().get()) {
			drawWide(context, client, "SPACE", 0, nextRow);
			nextRow += KEY_SIZE + GAP;
		}

		if (module.getMouse().get()) {
			drawWide(context, client, "LMB", 0, nextRow);
			drawKey(context, client, "RMB", 31, nextRow);
		}

		matrices.pop();
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

	private void drawWide(DrawContext context, MinecraftClient client, String label, int x, int y) {
		int width = 28;
		double active = animation(label).get();
		TextRenderer renderer = client.textRenderer;

		RenderUtils.roundedRect(context, x, y, x + width, y + KEY_SIZE, 4, RenderUtils.lerpColor(0x8C0B0E14, 0xE67C5CFF, active));
		RenderUtils.outline(context, x, y, x + width, y + KEY_SIZE, RenderUtils.lerpColor(0x66232B38, 0xFFB9A8FF, active));
		RenderUtils.centeredText(context, renderer, label, x + width / 2, y + 4, RenderUtils.lerpColor(0xFFE6EAF2, 0xFFFFFFFF, active));
	}
}
