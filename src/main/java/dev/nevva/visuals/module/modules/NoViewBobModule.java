package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class NoViewBobModule extends OptionModule<Boolean> {
	public NoViewBobModule() {
		super("No View Bob", "Disables the camera bobbing while walking", Category.RENDER);
	}

	@Override
	protected SimpleOption<Boolean> getOption(MinecraftClient client) {
		return client.options.getBobView();
	}

	@Override
	protected Boolean getOverrideValue(MinecraftClient client) {
		return Boolean.FALSE;
	}
}
