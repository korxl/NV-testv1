package dev.nevva.visuals.ui;

/**
 * Colour palette of the interface. Five presets plus a custom accent.
 */
public final class Theme {
	public final String name;
	public final int background;
	public final int sidebar;
	public final int column;
	public final int card;
	public final int border;
	public final int text;
	public final int dim;
	public final int accent;
	public final int accent2;
	public final int track;

	private Theme(String name, int background, int sidebar, int column, int card, int border,
				  int text, int dim, int accent, int accent2, int track) {
		this.name = name;
		this.background = background;
		this.sidebar = sidebar;
		this.column = column;
		this.card = card;
		this.border = border;
		this.text = text;
		this.dim = dim;
		this.accent = accent;
		this.accent2 = accent2;
		this.track = track;
	}

	public static Theme of(String name, float accentHue) {
		String key = name == null ? "Nursultan" : name;

		return switch (key) {
			case "Sadness" -> new Theme("Sadness", 0xF00D0A14, 0xFF120E1C, 0xFF171126, 0xFF1E1630,
					0xFF2E2345, 0xFFF2EBFF, 0xFF9A8FB8, 0xFFA855F7, 0xFFEC4899, 0xFF2A2140);
			case "Rockstar" -> new Theme("Rockstar", 0xF00F0D08, 0xFF141109, 0xFF1A1609, 0xFF221D0D,
					0xFF3A2F14, 0xFFFBF3DC, 0xFFB3A374, 0xFFF5C542, 0xFFE8A33D, 0xFF2E2712);
			case "Delta" -> new Theme("Delta", 0xF00A0F18, 0xFF0C1220, 0xFF111A2C, 0xFF152036,
					0xFF1F2D47, 0xFFE8F0FF, 0xFF8095B5, 0xFF3B82F6, 0xFF60A5FA, 0xFF1B2740);
			case "Wex" -> new Theme("Wex", 0xF0060A07, 0xFF080D09, 0xFF0C140E, 0xFF101A13,
					0xFF16311F, 0xFFE7FFEE, 0xFF6E9A7E, 0xFF39FF88, 0xFF9DFF57, 0xFF12251A);
			case "Custom" -> {
				int accent = hsv(accentHue, 0.68f, 1.0f);
				int accent2 = hsv(accentHue + 35.0f, 0.62f, 1.0f);
				yield new Theme("Custom", 0xF00A0B10, 0xFF0D0E14, 0xFF111319, 0xFF161821,
						0xFF232630, 0xFFEDEEF4, 0xFF8B8FA0, 0xFF000000 | accent, 0xFF000000 | accent2, 0xFF1C1F28);
			}
			default -> new Theme("Nursultan", 0xF00B1016, 0xFF0E141B, 0xFF111A22, 0xFF15212B,
					0xFF1B2A33, 0xFFE6F5F8, 0xFF7C97A1, 0xFF22D3EE, 0xFF38BDF8, 0xFF182831);
		};
	}

	/** Hue (0..360) to 0xRRGGBB. */
	public static int hsv(float hue, float saturation, float value) {
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
