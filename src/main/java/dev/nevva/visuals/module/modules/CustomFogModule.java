package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class CustomFogModule extends Module {
	private final NumberSetting fogStart = add(new NumberSetting("fog_start", "Start", 40.0, 0.0, 300.0, 1.0));
	private final NumberSetting fogEnd = add(new NumberSetting("fog_end", "End", 300.0, 20.0, 600.0, 5.0));
	private final ColorSetting color = add(new ColorSetting("fog_color", "Colour", 210.0f));
	private final BoolSetting tint = add(new BoolSetting("fog_tint", "Custom colour", true));

	public CustomFogModule() {
		super("Custom Fog", "Fog distance and colour", Category.WORLD);
	}

	public NumberSetting getFogStart() {
		return fogStart;
	}

	public NumberSetting getFogEnd() {
		return fogEnd;
	}

	public ColorSetting getColor() {
		return color;
	}

	public BoolSetting getTint() {
		return tint;
	}
}
