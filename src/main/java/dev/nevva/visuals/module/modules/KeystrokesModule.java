package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

public class KeystrokesModule extends Module {
	public KeystrokesModule() {
		super("Keystrokes", "Animated WASD and mouse key display", Category.HUD);
		setEnabled(true, false);
	}
}
