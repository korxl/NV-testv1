package dev.nevva.visuals.module;

import dev.nevva.visuals.config.Config;
import dev.nevva.visuals.module.modules.ArrayListModule;
import dev.nevva.visuals.module.modules.AspectRatioModule;
import dev.nevva.visuals.module.modules.CinematicModule;
import dev.nevva.visuals.module.modules.CustomFogModule;
import dev.nevva.visuals.module.modules.CustomWorldModule;
import dev.nevva.visuals.module.modules.FullbrightModule;
import dev.nevva.visuals.module.modules.HudModule;
import dev.nevva.visuals.module.modules.InterfaceModule;
import dev.nevva.visuals.module.modules.KeystrokesModule;
import dev.nevva.visuals.module.modules.MusicWidgetModule;
import dev.nevva.visuals.module.modules.NoDamageTiltModule;
import dev.nevva.visuals.module.modules.NoDarknessModule;
import dev.nevva.visuals.module.modules.NoNauseaModule;
import dev.nevva.visuals.module.modules.NoViewBobModule;
import dev.nevva.visuals.module.modules.ParticlesModule;
import dev.nevva.visuals.module.modules.TargetHudModule;
import dev.nevva.visuals.module.modules.UiStubModule;
import dev.nevva.visuals.module.modules.WatermarkModule;
import dev.nevva.visuals.module.modules.ZoomModule;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.module.setting.NumberSetting;
import dev.nevva.visuals.module.setting.Setting;

import net.minecraft.client.MinecraftClient;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModuleManager {
	private final List<Module> modules = new ArrayList<>();
	private final Set<Module> pressedBinds = new HashSet<>();

	public ModuleManager() {
		register(new HudModule());
		register(new TargetHudModule());
		register(new KeystrokesModule());
		register(new WatermarkModule());
		register(new ArrayListModule());
		register(new MusicWidgetModule());

		register(new ZoomModule());
		register(new FullbrightModule());
		register(new AspectRatioModule());
		register(new NoViewBobModule());
		register(new NoDamageTiltModule());
		register(new NoNauseaModule());
		register(new NoDarknessModule());
		register(new CinematicModule());

		register(new CustomWorldModule());
		register(new CustomFogModule());
		register(new ParticlesModule());
		register(new InterfaceModule());

		registerPreview("Player ESP", "Player outline and label controls", Category.RENDER,
				new ChoiceSetting("preview_player_esp_mode", "Style", "Outline", "Outline", "Box", "Glow"),
				new BoolSetting("preview_player_esp_names", "Names", true),
				new ColorSetting("preview_player_esp_color", "Tint", 205.0f));
		registerPreview("Item ESP", "Dropped item marker controls", Category.RENDER,
				new ChoiceSetting("preview_item_esp_mode", "Marker", "Box", "Box", "Label", "Glow"),
				new NumberSetting("preview_item_esp_range", "Range", 32.0, 8.0, 96.0, 4.0));
		registerPreview("Chams", "Model tint and outline controls", Category.RENDER,
				new ChoiceSetting("preview_chams_style", "Style", "Tint", "Tint", "Outline", "Flat"),
				new ColorSetting("preview_chams_color", "Tint colour", 205.0f));

		registerPreview("Aim Assist", "Targeting controls", Category.COMBAT,
				new ChoiceSetting("preview_aim_mode", "Mode", "Smooth", "Smooth", "Linear", "Soft"),
				new NumberSetting("preview_aim_fov", "Field of view", 60.0, 10.0, 180.0, 5.0),
				new ChoiceSetting("preview_aim_target", "Targets", "Players", "Players", "All entities"));
		registerPreview("Auto Clicker", "Click rate controls", Category.COMBAT,
				new NumberSetting("preview_clicker_cps", "Clicks per second", 10.0, 1.0, 20.0, 1.0),
				new BoolSetting("preview_clicker_hold", "Hold to activate", true));
		registerPreview("Reach", "Range controls", Category.COMBAT,
				new NumberSetting("preview_reach_distance", "Distance", 3.5, 3.0, 6.0, 0.1),
				new ChoiceSetting("preview_reach_mode", "Mode", "Standard", "Standard", "Extended"));

		registerPreview("Sprint", "Sprint preference controls", Category.MOVEMENT,
				new ChoiceSetting("preview_sprint_mode", "Mode", "Hold", "Hold", "Toggle", "Always"),
				new BoolSetting("preview_sprint_sneak", "Allow while sneaking", false));
		registerPreview("Speed", "Movement speed controls", Category.MOVEMENT,
				new NumberSetting("preview_speed_multiplier", "Multiplier", 1.2, 1.0, 3.0, 0.1),
				new ChoiceSetting("preview_speed_mode", "Mode", "Normal", "Normal", "Strafe", "Ground"));
		registerPreview("Flight", "Flight preference controls", Category.MOVEMENT,
				new NumberSetting("preview_flight_speed", "Speed", 0.5, 0.1, 1.0, 0.1),
				new ChoiceSetting("preview_flight_mode", "Mode", "Creative", "Creative", "Glide", "Hover"));
	}

	private void registerPreview(String name, String description, Category category, Setting<?>... settings) {
		register(new UiStubModule(name, description, category, settings));
	}

	public void register(Module module) {
		modules.add(module);
	}

	public List<Module> getModules() {
		return Collections.unmodifiableList(modules);
	}

	public <T extends Module> T get(Class<T> type) {
		for (Module module : modules) {
			if (type.isInstance(module)) {
				return type.cast(module);
			}
		}

		return null;
	}

	public void onTick(MinecraftClient client) {
		long window = client.getWindow().getHandle();

		for (Module module : modules) {
			int keyCode = module.getKeyCode();
			boolean pressed = keyCode >= 0 && GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
			boolean wasPressed = pressedBinds.contains(module);

			if (pressed) {
				pressedBinds.add(module);
			} else {
				pressedBinds.remove(module);
			}

			if (pressed && !wasPressed && client.currentScreen == null) {
				module.toggle();
			}

			module.onTick(client);
		}
	}

	public void applyConfig(Config config) {
		for (Module module : modules) {
			Boolean enabled = config.modules.get(module.getName());

			if (enabled != null) {
				module.setEnabled(enabled, false);
			}

			Integer bind = config.binds.get(module.getName());
			if (bind != null) module.setKeyCode(bind);

			for (Setting<?> setting : module.getSettings()) {
				if (setting instanceof BoolSetting bool) {
					Boolean value = config.bools.get(bool.getKey());
					if (value != null) bool.set(value);
				} else if (setting instanceof NumberSetting number) {
					Double value = config.numbers.get(number.getKey());
					if (value != null) number.set(value);
				} else if (setting instanceof ColorSetting color) {
					Double value = config.numbers.get(color.getKey());
					if (value != null) color.setHue(value.floatValue());
				} else if (setting instanceof ChoiceSetting choice) {
					String value = config.choices.get(choice.getKey());
					if (value != null) {
						if ("theme".equals(choice.getKey())) {
							value = switch (value) {
								case "Nursultan" -> "Aurora";
								case "Sadness" -> "Amethyst";
								case "Rockstar" -> "Amber";
								case "Delta" -> "Ocean";
								case "Wex" -> "Emerald";
								default -> value;
							};
						}
						choice.setValue(value);
					}
				}
			}
		}
	}

	public void writeTo(Config config) {
		config.modules.clear();
		config.bools.clear();
		config.numbers.clear();
		config.choices.clear();
		config.binds.clear();

		for (Module module : modules) {
			config.modules.put(module.getName(), module.isEnabled());
			config.binds.put(module.getName(), module.getKeyCode());

			for (Setting<?> setting : module.getSettings()) {
				if (setting instanceof BoolSetting bool) {
					config.bools.put(bool.getKey(), bool.get());
				} else if (setting instanceof NumberSetting number) {
					config.numbers.put(number.getKey(), number.get());
				} else if (setting instanceof ColorSetting color) {
					config.numbers.put(color.getKey(), (double) color.getHue());
				} else if (setting instanceof ChoiceSetting choice) {
					config.choices.put(choice.getKey(), choice.getValue());
				}
			}
		}
	}
}
