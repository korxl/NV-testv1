package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;
import dev.nevva.visuals.module.setting.ChoiceSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.particle.ParticlesMode;

public class ParticlesModule extends OptionModule<ParticlesMode> {
	private final ChoiceSetting mode = add(new ChoiceSetting("particles_mode", "Mode", "DECREASED",
			"ALL", "DECREASED", "MINIMAL"));

	public ParticlesModule() {
		super("Particles", "Controls how many world particles are rendered", Category.WORLD);
	}

	public ChoiceSetting getMode() {
		return mode;
	}

	@Override
	protected SimpleOption<ParticlesMode> getOption(MinecraftClient client) {
		return client.options.getParticles();
	}

	@Override
	protected ParticlesMode getOverrideValue(MinecraftClient client) {
		return ParticlesMode.valueOf(mode.getValue());
	}
}
