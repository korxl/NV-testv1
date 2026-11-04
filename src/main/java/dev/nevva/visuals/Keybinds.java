package dev.nevva.visuals;

import dev.nevva.visuals.NevvaVisuals;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

public final class Keybinds {
	public static final String CATEGORY = "category.nevvavisuals";

	public static KeyBinding MENU;
	public static KeyBinding ZOOM;
	public static KeyBinding HUD;

	private Keybinds() {
	}

	public static void register() {
		MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.nevvavisuals.menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				CATEGORY
		));

		ZOOM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.nevvavisuals.zoom",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_C,
				CATEGORY
		));

		HUD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.nevvavisuals.hud",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				CATEGORY
		));

		NevvaVisuals.LOGGER.info("Keybinds registered");
	}
}
