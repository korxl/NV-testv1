package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

public class TargetHudModule extends Module {
	public TargetHudModule() {
		super("Target HUD", "Shows the last player you hit for 3 seconds", Category.HUD);
		setEnabled(true, false);
	}
}
