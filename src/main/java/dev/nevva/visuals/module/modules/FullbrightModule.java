package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class FullbrightModule extends OptionModule<Double> {
	private final NumberSetting gamma = add(new NumberSetting("fullbright_gamma", "Gamma", 10.0, 1.0, 20.0, 0.5));

	public FullbrightModule() {
		super("Fullbright", "Lights up the world by boosting the brightness option", Category.RENDER);
	}

	public NumberSetting getGamma() {
		return gamma;
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getGamma();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return gamma.get();
	}
}
