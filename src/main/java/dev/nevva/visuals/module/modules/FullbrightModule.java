package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class FullbrightModule extends OptionModule<Double> {
	private static final double BOOSTED_GAMMA = 10.0;

	public FullbrightModule() {
		super("Fullbright", "Lights up the world by boosting the brightness option", Category.RENDER);
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getGamma();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return BOOSTED_GAMMA;
	}
}
