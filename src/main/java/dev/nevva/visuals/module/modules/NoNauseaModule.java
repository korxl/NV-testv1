package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoNauseaModule extends OptionModule<Double> {
	public NoNauseaModule() {
		super("No Nausea", "Removes the portal and nausea screen distortion", Category.RENDER);
	}

	@Override
	protected SimpleOption<Double> getOption(MinecraftClient client) {
		return client.options.getDistortionEffectScale();
	}

	@Override
	protected Double getOverrideValue(MinecraftClient client) {
		return 0.0;
	}
}
