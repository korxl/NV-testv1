package dev.nevva.visuals.module;

import dev.nevva.visuals.config.Config;
import dev.nevva.visuals.module.modules.ArrayListModule;
import dev.nevva.visuals.module.modules.AspectRatioModule;
import dev.nevva.visuals.module.modules.AutoSprintModule;
import dev.nevva.visuals.module.modules.CustomFogModule;
import dev.nevva.visuals.module.modules.CustomWorldModule;
import dev.nevva.visuals.module.modules.FullbrightModule;
import dev.nevva.visuals.module.modules.HudModule;
import dev.nevva.visuals.module.modules.InterfaceModule;
import dev.nevva.visuals.module.modules.KeystrokesModule;
import dev.nevva.visuals.module.modules.NoDamageTiltModule;
import dev.nevva.visuals.module.modules.NoDarknessModule;
import dev.nevva.visuals.module.modules.NoNauseaModule;
import dev.nevva.visuals.module.modules.NoViewBobModule;
import dev.nevva.visuals.module.modules.ParticlesModule;
import dev.nevva.visuals.module.modules.TargetHudModule;
import dev.nevva.visuals.module.modules.WatermarkModule;
import dev.nevva.visuals.module.modules.ZoomModule;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.module.setting.NumberSetting;
import dev.nevva.visuals.module.setting.Setting;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		register(new HudModule());
		register(new TargetHudModule());
		register(new KeystrokesModule());
		register(new WatermarkModule());
		register(new ArrayListModule());

		register(new ZoomModule());
		register(new FullbrightModule());
		register(new AspectRatioModule());
		register(new NoViewBobModule());
		register(new NoDamageTiltModule());
		register(new NoNauseaModule());
		register(new NoDarknessModule());

		register(new CustomWorldModule());
		register(new CustomFogModule());
		register(new ParticlesModule());

		register(new InterfaceModule());
		register(new AutoSprintModule());
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
		for (Module module : modules) {
			module.onTick(client);
		}
	}

	public void applyConfig(Config config) {
		for (Module module : modules) {
			Boolean enabled = config.modules.get(module.getName());

			if (enabled != null) {
				module.setEnabled(enabled, false);
			}

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
					if (value != null) choice.setValue(value);
				}
			}
		}
	}

	public void writeTo(Config config) {
		config.modules.clear();
		config.bools.clear();
		config.numbers.clear();
		config.choices.clear();

		for (Module module : modules) {
			config.modules.put(module.getName(), module.isEnabled());

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
