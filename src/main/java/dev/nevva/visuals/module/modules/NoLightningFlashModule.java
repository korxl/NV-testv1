package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoLightningFlashModule extends OptionModule<Boolean> {
	public NoLightningFlashModule() {
		super("No Lightning Flash", "Hides the white flash when lightning strikes", Category.RENDER);
	}

	@Override
	protected SimpleOption<Boolean> getOption(MinecraftClient client) {
		return client.options.getHideLightningFlashes();
	}

	@Override
	protected Boolean getOverrideValue(MinecraftClient client) {
		return Boolean.TRUE;
	}
}
