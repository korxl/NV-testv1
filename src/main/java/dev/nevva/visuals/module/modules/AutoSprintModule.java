package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;

public class AutoSprintModule extends Module {
	private final NumberSetting delay = add(new NumberSetting("sprint_delay", "Delay (ticks)", 0.0, 0.0, 20.0, 1.0));

	private int cooldown;

	public AutoSprintModule() {
		super("Auto Sprint", "Keeps you sprinting while you hold the forward key", Category.MISC);
	}

	public NumberSetting getDelay() {
		return delay;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null || client.options == null) {
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		if (!client.options.forwardKey.isPressed() || client.player.isSneaking() || client.player.isUsingItem()) {
			return;
		}

		client.player.setSprinting(true);
		cooldown = (int) Math.round(delay.get());
	}
}
