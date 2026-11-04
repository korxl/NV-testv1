package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

public class WatermarkModule extends Module {
	public WatermarkModule() {
		super("Watermark", "Animated NevvaVisuals logo", Category.HUD);
		setEnabled(true, false);
	}
}
