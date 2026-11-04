package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;

public class HudModule extends Module {
	private final BoolSetting background = add(new BoolSetting("hud_background", "Background", true));
	private final BoolSetting shadow = add(new BoolSetting("hud_shadow", "Text shadow", true));

	public HudModule() {
		super("HUD", "Info elements: drag them in the HUD editor", Category.HUD);
		setEnabled(true, false);
	}

	public BoolSetting getBackground() {
		return background;
	}

	public BoolSetting getShadow() {
		return shadow;
	}
}
