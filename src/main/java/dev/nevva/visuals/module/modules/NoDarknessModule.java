package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoDarknessModule extends OptionModule<Double> {
	public NoDarknessModule() {
		super("No Darkness", "Disables the warden / darkness effect overlay", Category.RENDER);
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getDarknessEffectScale();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return 0.0;
	}
}
