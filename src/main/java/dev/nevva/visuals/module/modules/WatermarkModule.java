package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

public class WatermarkModule extends Module {
	private final NumberSetting scale = add(new NumberSetting("watermark_scale", "Scale %", 100.0, 60.0, 160.0, 5.0));

	public WatermarkModule() {
		super("Watermark", "Animated NevvaVisuals logo", Category.HUD);
		setEnabled(true, false);
	}

	public NumberSetting getScale() {
		return scale;
	}
}
