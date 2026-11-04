package dev.nevva.visuals.ui;

/**
 * Colour palettes for the interface, including light, dark and glass presets.
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
		String key = name == null ? "Liquid Glass" : name;

		return switch (key) {
			case "White" -> new Theme("White", 0xFFF7F8FC, 0xFFF0F2F7, 0xFFF7F8FC, 0xFFFFFFFF,
					0xFFDCE1EB, 0xFF182033, 0xFF717A8D, 0xFF536DFA, 0xFF8D62F5, 0xFFE4E8F0);
			case "Dark" -> new Theme("Dark", 0xEF0A0D14, 0xF20F121B, 0xE5181D28, 0xD51E2431,
					0x66535E72, 0xFFF2F4FA, 0xFF929BAD, 0xFF8A6BFF, 0xFF43D9F5, 0xB1232937);
			case "Liquid Glass" -> new Theme("Liquid Glass", 0xE90A1020, 0xED0D1524, 0xD8152033, 0xC91C2B40,
					0x765B8BC2, 0xFFF0F7FF, 0xFF91A9C4, 0xFF4E9BFF, 0xFF72E4F7, 0xB11C3048);
			case "Amethyst" -> new Theme("Amethyst", 0xC90D0A16, 0xD5120E1E, 0xB8171128, 0xB81E1632,
					0x704B376B, 0xFFF2EBFF, 0xFF9A8FB8, 0xFFB65CFF, 0xFFEE58B8, 0xB12A2142);
			case "Amber" -> new Theme("Amber", 0xC90F0D08, 0xD5141109, 0xB81A1609, 0xB8221D0D,
					0x70584A21, 0xFFFBF3DC, 0xFFB3A374, 0xFFF5C542, 0xFFE8A33D, 0xB12E2712);
			case "Ocean" -> new Theme("Ocean", 0xC90A0F18, 0xD50C1220, 0xB8111A2C, 0xB8152036,
					0x70405E91, 0xFFE8F0FF, 0xFF8095B5, 0xFF3B82F6, 0xFF60A5FA, 0xB11B2740);
			case "Emerald" -> new Theme("Emerald", 0xC9060A07, 0xD5080D09, 0xB80C140E, 0xB8101A13,
					0x70408054, 0xFFE7FFEE, 0xFF6E9A7E, 0xFF39FF88, 0xFF9DFF57, 0xB112251A);
			case "Custom" -> {
				int accent = hsv(accentHue, 0.68f, 1.0f);
				int accent2 = hsv(accentHue + 35.0f, 0.62f, 1.0f);
				yield new Theme("Custom", 0xC90A0B10, 0xD50D0E14, 0xB8111319, 0xB8161821,
						0x70454A5D, 0xFFEDEEF4, 0xFF8B8FA0, 0xFF000000 | accent, 0xFF000000 | accent2, 0xB11C1F28);
			}
			case "Aurora" -> new Theme("Aurora", 0xC909101B, 0xD50C1421, 0xB8101B2B, 0xB8152234,
					0x704A9DD0, 0xFFEAF5FF, 0xFF91A8BD, 0xFF278CFF, 0xFF59D6FF, 0xB1182B42);
			default -> new Theme("Liquid Glass", 0xE90A1020, 0xED0D1524, 0xD8152033, 0xC91C2B40,
					0x765B8BC2, 0xFFF0F7FF, 0xFF91A9C4, 0xFF4E9BFF, 0xFF72E4F7, 0xB11C3048);
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
