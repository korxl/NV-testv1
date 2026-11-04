package dev.nevva.visuals.ui;

/**
 * Text styling helpers. Minecraft ships a single font, so "font" here switches
 * between rendering styles rather than shipping third-party font files.
 */
public final class Fonts {
	public static final String DEFAULT = "Default";
	public static final String CAPS = "Caps";
	public static final String SOFT = "Soft";

	private static String current = DEFAULT;

	private Fonts() {
	}

	public static void set(String font) {
		current = font == null ? DEFAULT : font;
	}

	public static String get() {
		return current;
	}

	public static String transform(String text) {
		if (text == null) {
			return "";
		}

		if (CAPS.equals(current)) {
			return text.toUpperCase(java.util.Locale.ROOT);
		}

		return text;
	}

	public static boolean shadow() {
		return !SOFT.equals(current);
	}
}
