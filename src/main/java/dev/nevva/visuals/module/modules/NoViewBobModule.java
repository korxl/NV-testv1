package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.OptionModule;
import dev.nevva.visuals.module.setting.BoolSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;

public class NoViewBobModule extends OptionModule<Boolean> {
	private final BoolSetting firstPerson = add(new BoolSetting("bob_first_person", "First person only", true));

	public NoViewBobModule() {
		super("No View Bob", "Disables the camera bobbing while walking", Category.RENDER);
	}

	public BoolSetting getFirstPerson() {
		return firstPerson;
	}

	@Override
	protected boolean shouldApply(MinecraftClient client) {
		if (client.options == null) {
			return false;
		}

		return !firstPerson.get() || client.options.getPerspective() == Perspective.FIRST_PERSON;
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
