package dev.nevva.visuals.module;

/**
 * Groups modules in the in-game menu.
 */
public enum Category {
	HUD("HUD", "hud"),
	RENDER("Render", "render"),
	WORLD("World", "world"),
	CLIENT("Client", "client"),
	MISC("Misc", "misc");

	private final String label;
	private final String icon;

	Category(String label, String icon) {
		this.label = label;
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}
}
