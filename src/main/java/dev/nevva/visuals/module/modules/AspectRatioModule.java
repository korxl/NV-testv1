package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

public class AspectRatioModule extends Module {
	private final NumberSetting aspect = add(new NumberSetting("aspect_ratio", "Aspect", 1.7778, 0.5, 3.0, 0.01));

	public AspectRatioModule() {
		super("Aspect Ratio", "Stretches the world view to a custom aspect ratio", Category.RENDER);
	}

	public NumberSetting getAspect() {
		return aspect;
	}
}
