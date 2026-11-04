package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class KeystrokesModule extends Module {
	private final NumberSetting scale = add(new NumberSetting("keystroke_scale", "Scale %", 100.0, 60.0, 160.0, 5.0));
	private final BoolSetting mouse = add(new BoolSetting("keystroke_mouse", "Mouse buttons", true));
	private final BoolSetting space = add(new BoolSetting("keystroke_space", "Space bar", true));

	public KeystrokesModule() {
		super("Keystrokes", "Animated WASD, space and mouse key display", Category.HUD);
		setEnabled(true, false);
	}

	public NumberSetting getScale() {
		return scale;
	}

	public BoolSetting getMouse() {
		return mouse;
	}

	public BoolSetting getSpace() {
		return space;
	}
}
