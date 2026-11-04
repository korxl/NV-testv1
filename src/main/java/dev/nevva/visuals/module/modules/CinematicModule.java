package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

import net.minecraft.client.MinecraftClient;

public class CinematicModule extends Module {
	private boolean applied;

	public CinematicModule() {
		super("Cinematic", "Hides the vanilla HUD and the NevvaVisuals overlay", Category.RENDER);
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (client.options == null) {
			return;
		}

		if (isEnabled()) {
			if (!client.options.hudHidden) {
				client.options.hudHidden = true;
			}

			applied = true;
		} else if (applied) {
			client.options.hudHidden = false;
			applied = false;
		}
	}
}
