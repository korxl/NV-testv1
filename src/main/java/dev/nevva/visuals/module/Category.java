package dev.nevva.visuals.module;

/**
 * Groups modules in the in-game menu.
 */
public enum Category {
	HUD("HUD"),
	RENDER("Render"),
	WORLD("World"),
	PLAYER("Player");

	private final String label;

	Category(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
