package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoDarknessModule extends OptionModule<Double> {
	private final NumberSetting strength = add(new NumberSetting("darkness_strength", "Strength %", 100.0, 0.0, 100.0, 5.0));

	public NoDarknessModule() {
		super("No Darkness", "Disables the warden / darkness effect overlay", Category.RENDER);
	}

	public NumberSetting getStrength() {
		return strength;
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getDarknessEffectScale();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return 1.0 - strength.get() / 100.0;
	}
}
