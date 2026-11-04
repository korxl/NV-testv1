package dev.nevva.visuals.hud;

/**
 * One draggable line of the info HUD.
 */
public final class HudElement {
	public final String id;
	public final String label;
	public final double defaultX;
	public final double defaultY;

	public boolean enabled = true;
	public double x;
	public double y;
	public double scale = 1.0;

	// Filled in by the renderer, used by the editor for hit testing.
	public int lastX;
	public int lastY;
	public int lastWidth;
	public int lastHeight;

	public HudElement(String id, String label, double x, double y, boolean enabled) {
		this.id = id;
		this.label = label;
		this.defaultX = x;
		this.defaultY = y;
		this.x = x;
		this.y = y;
		this.enabled = enabled;
	}

	public void reset() {
		x = defaultX;
		y = defaultY;
		scale = 1.0;
	}
}
