package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoDamageTiltModule extends OptionModule<Double> {
	public NoDamageTiltModule() {
		super("No Damage Tilt", "Removes the camera tilt when you get hurt", Category.RENDER);
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getDamageTiltStrength();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return 0.0;
	}
}
