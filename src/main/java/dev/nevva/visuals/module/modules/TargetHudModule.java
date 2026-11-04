package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class TargetHudModule extends Module {
	private final NumberSetting scale = add(new NumberSetting("target_scale", "Scale %", 100.0, 60.0, 160.0, 5.0));
	private final BoolSetting distance = add(new BoolSetting("target_distance", "Distance", true));
	private final BoolSetting healthBar = add(new BoolSetting("target_health_bar", "Health bar", true));

	public TargetHudModule() {
		super("Target HUD", "Shows the last player you hit for 3 seconds", Category.HUD);
		setEnabled(true, false);
	}

	public NumberSetting getScale() {
		return scale;
	}

	public BoolSetting getDistance() {
		return distance;
	}

	public BoolSetting getHealthBar() {
		return healthBar;
	}
}
