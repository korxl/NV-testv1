package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoDamageTiltModule extends OptionModule<Double> {
	private final NumberSetting strength = add(new NumberSetting("damage_tilt_strength", "Strength %", 100.0, 0.0, 100.0, 5.0));

	public NoDamageTiltModule() {
		super("No Damage Tilt", "Removes the camera tilt when you get hurt", Category.RENDER);
	}

	public NumberSetting getStrength() {
		return strength;
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getDamageTiltStrength();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return 1.0 - strength.get() / 100.0;
	}
}
