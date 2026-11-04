package dev.nevva.visuals.hud;

import dev.nevva.visuals.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Position, visibility and scale of every info HUD line, saved in the config.
 */
public final class HudLayout {
	private final List<HudElement> elements = new ArrayList<>();

	public HudLayout() {
		add("fps", "FPS", 0.006, 0.030, true);
		add("ping", "Ping", 0.006, 0.065, true);
		add("coords", "Coordinates", 0.006, 0.100, true);
		add("direction", "Compass", 0.006, 0.135, true);
		add("speed", "Speed", 0.006, 0.170, true);
		add("cps", "CPS", 0.006, 0.205, false);
		add("armor", "Armor", 0.006, 0.240, false);
		add("effects", "Effects", 0.006, 0.275, false);
		add("tps", "TPS", 0.006, 0.310, false);
		add("memory", "Memory", 0.006, 0.345, false);
		add("time", "Time", 0.006, 0.380, false);
	}

	private void add(String id, String label, double x, double y, boolean enabled) {
		elements.add(new HudElement(id, label, x, y, enabled));
	}

	public List<HudElement> elements() {
		return elements;
	}

	public HudElement byId(String id) {
		for (HudElement element : elements) {
			if (element.id.equals(id)) {
				return element;
			}
		}

		return null;
	}

	public void load(Config config) {
		for (HudElement element : elements) {
			Boolean enabled = config.bools.get("hud_" + element.id);

			if (enabled != null) {
				element.enabled = enabled;
			}

			Double x = config.numbers.get("hud_" + element.id + "_x");
			Double y = config.numbers.get("hud_" + element.id + "_y");
			Double scale = config.numbers.get("hud_" + element.id + "_scale");

			if (x != null) element.x = x;
			if (y != null) element.y = y;
			if (scale != null) element.scale = scale;
		}
	}

	public void save(Config config) {
		for (HudElement element : elements) {
			config.bools.put("hud_" + element.id, element.enabled);
			config.numbers.put("hud_" + element.id + "_x", element.x);
			config.numbers.put("hud_" + element.id + "_y", element.y);
			config.numbers.put("hud_" + element.id + "_scale", element.scale);
		}
	}

	public void resetAll() {
		for (HudElement element : elements) {
			element.reset();
			element.enabled = element.defaultY <= 0.205;
		}
	}
}
