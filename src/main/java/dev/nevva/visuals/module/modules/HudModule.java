package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

public class HudModule extends Module {
	public HudModule() {
		super("HUD", "FPS, coordinates, ping, speed, TPS and the active module list", Category.HUD);
		setEnabled(true, false);
	}
}
