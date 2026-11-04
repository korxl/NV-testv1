package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;

public class AutoRespawnModule extends Module {
	private final NumberSetting delay = add(new NumberSetting("respawn_delay", "Delay (ticks)", 10.0, 0.0, 100.0, 1.0));

	private int cooldown;

	public AutoRespawnModule() {
		super("Auto Respawn", "Respawns automatically after death", Category.MISC);
	}

	public NumberSetting getDelay() {
		return delay;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null) {
			cooldown = 0;
			return;
		}

		if (!client.player.isDead()) {
			cooldown = (int) Math.round(delay.get());
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		client.player.requestRespawn();
		cooldown = (int) Math.round(delay.get());
	}
}
