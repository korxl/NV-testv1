package dev.nevva.visuals.module.setting;

/**
 * A colour picked with a hue slider in the menu. Stored as the hue angle.
 */
public final class ColorSetting extends Setting<Integer> {
	private float hue;

	public ColorSetting(String key, String label, float defaultHue) {
		super(key, label);
		this.hue = defaultHue;
	}

	@Override
	public Integer getValue() {
		return getColor();
	}

	public float getHue() {
		return hue;
	}

	public void setHue(float hue) {
		this.hue = ((hue % 360.0f) + 360.0f) % 360.0f;
	}

	/** 0xRRGGBB, fully saturated and bright so tints stay visible. */
	public int getColor() {
		return hsvToRgb(hue, 0.72f, 1.0f);
	}

	public static int hsvToRgb(float hue, float saturation, float value) {
		float h = ((hue % 360.0f) + 360.0f) % 360.0f / 60.0f;
		int sector = (int) Math.floor(h);
		float fraction = h - sector;
		float p = value * (1.0f - saturation);
		float q = value * (1.0f - saturation * fraction);
		float t = value * (1.0f - saturation * (1.0f - fraction));

		float r;
		float g;
		float b;

		switch (sector % 6) {
			case 0 -> {
				r = value;
				g = t;
				b = p;
			}
			case 1 -> {
				r = q;
				g = value;
				b = p;
			}
			case 2 -> {
				r = p;
				g = value;
				b = t;
			}
			case 3 -> {
				r = p;
				g = q;
				b = value;
			}
			case 4 -> {
				r = t;
				g = p;
				b = value;
			}
			default -> {
				r = value;
				g = p;
				b = q;
			}
		}

		return ((int) (r * 255.0f) << 16) | ((int) (g * 255.0f) << 8) | (int) (b * 255.0f);
	}
}
