package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;

public class FastPlaceModule extends Module {
	private final NumberSetting delay = add(new NumberSetting("fastplace_delay", "Cooldown", 0.0, 0.0, 4.0, 1.0));

	public FastPlaceModule() {
		super("Fast Place", "Removes the right click delay between placements", Category.MISC);
	}

	public NumberSetting getDelay() {
		return delay;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null || client.options == null) {
			return;
		}

		int target = (int) Math.round(delay.get());

		if (client.itemUseCooldown > target) {
			client.itemUseCooldown = target;
		}
	}
}
