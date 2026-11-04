package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class CustomWorldModule extends Module {
	private final BoolSetting customTime = add(new BoolSetting("world_custom_time", "Custom time", true));
	private final NumberSetting worldTime = add(new NumberSetting("world_time", "Time of day", 6000.0, 0.0, 23999.0, 1.0));
	private final BoolSetting skyTint = add(new BoolSetting("world_sky_tint", "Sky colour", false));
	private final ColorSetting skyColor = add(new ColorSetting("world_sky_color", "Sky hue", 205.0f));
	private final BoolSetting clearWeather = add(new BoolSetting("clear_weather", "Clear weather", true));

	public CustomWorldModule() {
		super("Custom World", "Client-side time, sky colour and weather", Category.WORLD);
	}

	public BoolSetting getCustomTime() {
		return customTime;
	}

	public NumberSetting getWorldTime() {
		return worldTime;
	}

	public BoolSetting getSkyTint() {
		return skyTint;
	}

	public ColorSetting getSkyColor() {
		return skyColor;
	}

	public BoolSetting getClearWeather() {
		return clearWeather;
	}
}
