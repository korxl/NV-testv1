package dev.nevva.visuals.hud;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.anim.Animation;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.modules.ArrayListModule;
import dev.nevva.visuals.module.modules.HudModule;
import dev.nevva.visuals.module.modules.InterfaceModule;
import dev.nevva.visuals.render.RenderUtils;
import dev.nevva.visuals.ui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classic client "ArrayList": enabled modules on one side, sliding in smoothly.
 */
public final class ArrayListHud {
	private final NevvaVisuals mod;
	private final Map<String, Animation> animations = new HashMap<>();

	public ArrayListHud(NevvaVisuals mod) {
		this.mod = mod;
	}

	public void render(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options == null || client.options.hudHidden || mod.getModuleManager() == null) {
			return;
		}

		ArrayListModule module = mod.getModuleManager().get(ArrayListModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		Theme theme = mod.getTheme();
		TextRenderer renderer = client.textRenderer;
		List<Module> enabled = new ArrayList<>();

		for (Module entry : mod.getModuleManager().getModules()) {
			if (!entry.isEnabled() || entry instanceof ArrayListModule
					|| entry instanceof HudModule || entry instanceof InterfaceModule) {
				continue;
			}

			enabled.add(entry);
		}

		enabled.sort(Comparator.comparingInt((Module m) -> renderer.getWidth(m.getName())).reversed());

		float scale = (float) (module.getScale().get() / 100.0);
		float effectiveWidth = client.getWindow().getScaledWidth() / scale;
		float effectiveHeight = client.getWindow().getScaledHeight() / scale;

		var matrices = context.getMatrices();
		matrices.push();
		matrices.scale(scale, scale, 1.0f);

		float y = 4.0f;

		for (Module entry : enabled) {
			Animation animation = animations.computeIfAbsent(entry.getName(), k -> new Animation(0.0, 14.0));
			animation.setTarget(1.0);
			animation.update();

			String name = entry.getName();
			int width = RenderUtils.width(renderer, name);
			float value = (float) animation.get();
			float rowWidth = width + 12.0f;
			float x = module.isRight()
					? effectiveWidth - rowWidth * value - 2.0f
					: -rowWidth * (1.0f - value) + 2.0f;

			RenderUtils.roundedRect(context, (int) x, (int) y, (int) (x + rowWidth), (int) (y + 11), 3, theme.card);

			if (module.getBar().get()) {
				int barX = module.isRight() ? (int) x : (int) (x + rowWidth) - 2;
				RenderUtils.roundedRect(context, barX, (int) y, barX + 2, (int) (y + 11), 1, theme.accent);
			}

			int textColor = module.getGradient().get()
					? RenderUtils.lerpColor(theme.accent, theme.accent2, Math.min(1.0, y / Math.max(1.0f, effectiveHeight)))
					: theme.text;

			RenderUtils.text(context, renderer, name, (int) (x + 6), (int) (y + 2), textColor);
			y += 12.0f;
		}

		matrices.pop();
	}
}
