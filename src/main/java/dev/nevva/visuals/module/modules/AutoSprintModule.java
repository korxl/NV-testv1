package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;

import net.minecraft.client.MinecraftClient;

public class AutoSprintModule extends Module {
	public AutoSprintModule() {
		super("Auto Sprint", "Keeps you sprinting while you hold the forward key", Category.PLAYER);
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null || client.options == null) {
			return;
		}

		if (!client.options.forwardKey.isPressed()) {
			return;
		}

		if (client.player.isSneaking() || client.player.isUsingItem()) {
			return;
		}

		client.player.setSprinting(true);
	}
}
