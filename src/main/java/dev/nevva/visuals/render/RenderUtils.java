package dev.nevva.visuals.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Small drawing helpers: rounded panels, gradients and text, so the HUD and the
 * menu look consistent without depending on any external UI library.
 */
public final class RenderUtils {
	private RenderUtils() {
	}

	public static void roundedRect(DrawContext context, int x1, int y1, int x2, int y2, int radius, int color) {
		if (x2 <= x1 || y2 <= y1 || (color >>> 24) == 0) {
			return;
		}

		int r = Math.max(0, Math.min(radius, Math.min((x2 - x1) / 2, (y2 - y1) / 2)));

		if (r <= 0) {
			context.fill(x1, y1, x2, y2, color);
			return;
		}

		context.fill(x1 + r, y1, x2 - r, y2, color);
		context.fill(x1, y1 + r, x1 + r, y2 - r, color);
		context.fill(x2 - r, y1 + r, x2, y2 - r, color);

		for (int i = 0; i < r; i++) {
			int dy = r - i;
			int dx = (int) Math.round(r - Math.sqrt((double) (r * r - dy * dy)));

			context.fill(x1 + dx, y1 + i, x2 - dx, y1 + i + 1, color);
			context.fill(x1 + dx, y2 - i - 1, x2 - dx, y2 - i, color);
		}
	}

	public static void gradientRect(DrawContext context, int x1, int y1, int x2, int y2, int topColor, int bottomColor) {
		int height = y2 - y1;

		if (height <= 0 || x2 <= x1) {
			return;
		}

		for (int i = 0; i < height; i++) {
			double t = height == 1 ? 0.0 : (double) i / (height - 1);
			context.fill(x1, y1 + i, x2, y1 + i + 1, lerpColor(topColor, bottomColor, t));
		}
	}

	public static void outline(DrawContext context, int x1, int y1, int x2, int y2, int color) {
		context.fill(x1, y1, x2, y1 + 1, color);
		context.fill(x1, y2 - 1, x2, y2, color);
		context.fill(x1, y1, x1 + 1, y2, color);
		context.fill(x2 - 1, y1, x2, y2, color);
	}

	public static int lerpColor(int from, int to, double t) {
		double clamped = Math.max(0.0, Math.min(1.0, t));

		int alpha = (int) Math.round(((from >>> 24) & 0xFF) + ((((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF)) * clamped));
		int red = (int) Math.round(((from >> 16) & 0xFF) + ((((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * clamped));
		int green = (int) Math.round(((from >> 8) & 0xFF) + ((((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * clamped));
		int blue = (int) Math.round((from & 0xFF) + (((to & 0xFF) - (from & 0xFF)) * clamped));

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static int withAlpha(int color, double alpha) {
		int a = (int) Math.round(255.0 * Math.max(0.0, Math.min(1.0, alpha)));
		return (a << 24) | (color & 0xFFFFFF);
	}

	public static void text(DrawContext context, TextRenderer renderer, String text, int x, int y, int color) {
		context.drawTextWithShadow(renderer, text, x, y, color);
	}

	public static void centeredText(DrawContext context, TextRenderer renderer, String text, int centerX, int y, int color) {
		context.drawTextWithShadow(renderer, text, centerX - renderer.getWidth(text) / 2, y, color);
	}
}
