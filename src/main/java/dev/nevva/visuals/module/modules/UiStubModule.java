package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.Setting;

/**
 * A menu-only module placeholder. It deliberately has no game behavior.
 */
public final class UiStubModule extends Module {
	public UiStubModule(String name, String description, Category category, Setting<?>... settings) {
		super(name, description + " UI-only preview; no in-game effect.", category);

		for (Setting<?> setting : settings) {
			getSettings().add(setting);
		}
	}
}
