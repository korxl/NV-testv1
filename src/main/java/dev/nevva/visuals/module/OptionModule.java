package dev.nevva.visuals.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

import java.util.Objects;

/**
 * A module that forces a vanilla game option to a value while enabled, remembering
 * the player's own value so it can be restored when the module is switched off.
 */
public abstract class OptionModule<T> extends Module {
	private boolean hasPrevious;
	private T previous;

	protected OptionModule(String name, String description, Category category) {
		super(name, description, category);
	}

	protected abstract SimpleOption<T> getOption(MinecraftClient client);

	protected abstract T getOverrideValue(MinecraftClient client);

	@Override
	public void onTick(MinecraftClient client) {
		if (client.options == null) {
			return;
		}

		SimpleOption<T> option = getOption(client);

		if (option == null) {
			return;
		}

		if (isEnabled()) {
			T target = getOverrideValue(client);

			if (!hasPrevious) {
				previous = option.getValue();
				hasPrevious = true;
			}

			if (!Objects.equals(option.getValue(), target)) {
				option.setValue(target);
			}
		} else if (hasPrevious) {
			if (previous != null) {
				option.setValue(previous);
			}

			hasPrevious = false;
		}
	}
}
