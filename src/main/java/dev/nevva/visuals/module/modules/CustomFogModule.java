package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

public class CustomFogModule extends Module {
	private final NumberSetting fogStart = add(new NumberSetting("fog_start", "Fog start", 40.0, 0.0, 300.0, 1.0));
	private final NumberSetting fogEnd = add(new NumberSetting("fog_end", "Fog end", 300.0, 20.0, 600.0, 5.0));

	public CustomFogModule() {
		super("Custom Fog", "Custom fog start and end distance", Category.RENDER);
	}

	public NumberSetting getFogStart() {
		return fogStart;
	}

	public NumberSetting getFogEnd() {
		return fogEnd;
	}
}
