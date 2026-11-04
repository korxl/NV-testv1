package dev.nevva.visuals.render;

import dev.nevva.visuals.ui.Fonts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Drawing helpers with anti-aliased rounded corners, soft shadows and simple icons.
 */
public final class RenderUtils {
	private RenderUtils() {
	}

	/** Filled rounded rectangle with one-pixel anti-aliased corners. */
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
			double dy = r - (i + 0.5);
			double inset = r - Math.sqrt(Math.max(0.0, r * (double) r - dy * dy));
			int left = x1 + (int) Math.floor(inset);
			double partial = inset - Math.floor(inset);
			int top = y1 + i;
			int bottom = y2 - i - 1;

			if (partial > 0.02) {
				int alpha = scaled(color, 1.0 - partial);
				context.fill(left, top, left + 1, top + 1, alpha);
				context.fill(left, bottom, left + 1, bottom + 1, alpha);
			}

			context.fill(left + 1, top, x1 + r, top + 1, color);
			context.fill(left + 1, bottom, x1 + r, bottom + 1, color);

			int right = x2 - (int) Math.floor(inset);

			if (partial > 0.02) {
				int alpha = scaled(color, 1.0 - partial);
				context.fill(right - 1, top, right, top + 1, alpha);
				context.fill(right - 1, bottom, right, bottom + 1, alpha);
			}

			context.fill(x2 - r, top, right - 1, top + 1, color);
			context.fill(x2 - r, bottom, right - 1, bottom + 1, color);
		}
	}

	/** Rounded panel with a 1px border and a soft outer glow. */
	public static void panel(DrawContext context, int x1, int y1, int x2, int y2, int radius, int fill, int border) {
		glow(context, x1, y1, x2, y2, radius, 0x40000000);
		roundedRect(context, x1, y1, x2, y2, radius, border);
		roundedRect(context, x1 + 1, y1 + 1, x2 - 1, y2 - 1, Math.max(0, radius - 1), fill);
	}

	/** Cheap soft shadow: a few expanding translucent rounded rectangles. */
	public static void glow(DrawContext context, int x1, int y1, int x2, int y2, int radius, int color) {
		for (int i = 3; i >= 1; i--) {
			int alpha = scaled(color, 1.0 / (i * 2.0));
			roundedRect(context, x1 - i, y1 - i, x2 + i, y2 + i, radius + i, alpha);
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

	public static void horizontalGradient(DrawContext context, int x1, int y1, int x2, int y2, int leftColor, int rightColor) {
		int width = x2 - x1;

		if (width <= 0 || y2 <= y1) {
			return;
		}

		for (int i = 0; i < width; i++) {
			double t = width == 1 ? 0.0 : (double) i / (width - 1);
			context.fill(x1 + i, y1, x1 + i + 1, y2, lerpColor(leftColor, rightColor, t));
		}
	}

	public static void outline(DrawContext context, int x1, int y1, int x2, int y2, int color) {
		context.fill(x1, y1, x2, y1 + 1, color);
		context.fill(x1, y2 - 1, x2, y2, color);
		context.fill(x1, y1, x1 + 1, y2, color);
		context.fill(x2 - 1, y1, x2, y2, color);
	}

	/** Anti-aliased filled circle, used for slider knobs and icons. */
	public static void circle(DrawContext context, double centerX, double centerY, double radius, int color) {
		if (radius <= 0.0 || (color >>> 24) == 0) {
			return;
		}

		int minY = (int) Math.floor(centerY - radius);
		int maxY = (int) Math.ceil(centerY + radius);

		for (int py = minY; py < maxY; py++) {
			double dy = py + 0.5 - centerY;
			double inner = radius * radius - dy * dy;

			if (inner <= 0.0) {
				continue;
			}

			double half = Math.sqrt(inner);
			double left = centerX - half;
			double right = centerX + half;
			int leftPixel = (int) Math.floor(left);
			int rightPixel = (int) Math.ceil(right);
			double leftCoverage = Math.max(0.0, Math.min(1.0, leftPixel + 1 - left));
			double rightCoverage = Math.max(0.0, Math.min(1.0, right - (rightPixel - 1)));

			if (rightPixel - 1 > leftPixel + 1) {
				context.fill(leftPixel + 1, py, rightPixel - 1, py + 1, color);
			}

			if (leftCoverage > 0.02) {
				context.fill(leftPixel, py, leftPixel + 1, py + 1, scaled(color, leftCoverage));
			}

			if (rightCoverage > 0.02) {
				context.fill(rightPixel - 1, py, rightPixel, py + 1, scaled(color, rightCoverage));
			}
		}
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

	/** Multiplies the existing alpha channel instead of replacing it. */
	public static int scaled(int color, double factor) {
		int a = (int) Math.round(((color >>> 24) & 0xFF) * Math.max(0.0, Math.min(1.0, factor)));
		return (a << 24) | (color & 0xFFFFFF);
	}

	public static void text(DrawContext context, TextRenderer renderer, String text, int x, int y, int color) {
		context.drawText(renderer, Fonts.transform(text), x, y, color, Fonts.shadow());
	}

	public static void textNoShadow(DrawContext context, TextRenderer renderer, String text, int x, int y, int color) {
		context.drawText(renderer, Fonts.transform(text), x, y, color, false);
	}

	public static void centeredText(DrawContext context, TextRenderer renderer, String text, int centerX, int y, int color) {
		String value = Fonts.transform(text);
		context.drawText(renderer, value, centerX - renderer.getWidth(value) / 2, y, color, Fonts.shadow());
	}

	public static int width(TextRenderer renderer, String text) {
		return renderer.getWidth(Fonts.transform(text));
	}

	/** Tiny vector icons drawn from primitives, so the menu needs no textures. */
	public static void icon(DrawContext context, String kind, int x, int y, int color) {
		switch (kind) {
			case "hud" -> {
				context.fill(x, y + 1, x + 8, y + 2, color);
				context.fill(x, y + 4, x + 6, y + 5, color);
				context.fill(x, y + 7, x + 4, y + 8, color);
			}
			case "render" -> {
				context.fill(x + 3, y, x + 6, y + 1, color);
				context.fill(x + 1, y + 1, x + 8, y + 2, color);
				context.fill(x, y + 2, x + 9, y + 6, color);
				context.fill(x + 1, y + 6, x + 8, y + 7, color);
				context.fill(x + 3, y + 7, x + 6, y + 8, color);
			}
			case "world" -> {
				context.fill(x + 2, y, x + 7, y + 1, color);
				context.fill(x, y + 2, x + 9, y + 6, color);
				context.fill(x + 2, y + 7, x + 7, y + 8, color);
			}
			case "client" -> {
				circle(context, x + 4.5, y + 4, 3.2, color);
				circle(context, x + 4.5, y + 4, 1.3, 0xFF000000 | (color & 0xFFFFFF));
			}
			default -> {
				context.fill(x + 1, y + 1, x + 3, y + 3, color);
				context.fill(x + 6, y + 1, x + 8, y + 3, color);
				context.fill(x + 1, y + 6, x + 3, y + 8, color);
				context.fill(x + 6, y + 6, x + 8, y + 8, color);
			}
		}
	}
}
