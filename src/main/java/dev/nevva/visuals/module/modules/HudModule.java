package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;

public class HudModule extends Module {
	private final BoolSetting fps = add(new BoolSetting("hud_fps", "FPS", true));
	private final BoolSetting coords = add(new BoolSetting("hud_coords", "Coordinates", true));
	private final BoolSetting direction = add(new BoolSetting("hud_direction", "Direction", true));
	private final BoolSetting speed = add(new BoolSetting("hud_speed", "Speed", true));
	private final BoolSetting ping = add(new BoolSetting("hud_ping", "Ping", true));
	private final BoolSetting tps = add(new BoolSetting("hud_tps", "TPS", true));
	private final BoolSetting memory = add(new BoolSetting("hud_memory", "Memory", false));
	private final BoolSetting background = add(new BoolSetting("hud_background", "Background", true));
	private final ChoiceSetting corner = add(new ChoiceSetting("hud_corner", "Position", "LEFT",
			"LEFT", "RIGHT"));

	public HudModule() {
		super("HUD", "Info overlay: FPS, coordinates, ping, speed and more", Category.HUD);
		setEnabled(true, false);
	}

	public BoolSetting getFps() {
		return fps;
	}

	public BoolSetting getCoords() {
		return coords;
	}

	public BoolSetting getDirection() {
		return direction;
	}

	public BoolSetting getSpeed() {
		return speed;
	}

	public BoolSetting getPing() {
		return ping;
	}

	public BoolSetting getTps() {
		return tps;
	}

	public BoolSetting getMemory() {
		return memory;
	}

	public BoolSetting getBackground() {
		return background;
	}

	public boolean isRight() {
		return "RIGHT".equals(corner.getValue());
	}
}
