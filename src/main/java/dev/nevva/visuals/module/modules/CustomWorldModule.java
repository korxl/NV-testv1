package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class CustomWorldModule extends Module {
	private final NumberSetting worldTime = add(new NumberSetting("world_time", "Time of day", 6000.0, 0.0, 23999.0, 1.0));
	private final BoolSetting clearWeather = add(new BoolSetting("clear_weather", "Clear weather", true));

	public CustomWorldModule() {
		super("Custom World", "Client-side time of day and clear weather", Category.WORLD);
	}

	public NumberSetting getWorldTime() {
		return worldTime;
	}

	public BoolSetting getClearWeather() {
		return clearWeather;
	}
}
