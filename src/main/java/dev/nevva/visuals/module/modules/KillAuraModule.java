package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class KillAuraModule extends Module {
	private final NumberSetting range = add(new NumberSetting("aura_range", "Range", 3.6, 2.5, 6.0, 0.1));
	private final NumberSetting cps = add(new NumberSetting("aura_cps", "CPS", 8.0, 1.0, 20.0, 1.0));
	private final BoolSetting swing = add(new BoolSetting("aura_swing", "Swing hand", true));

	private long lastAttack;

	public KillAuraModule() {
		super("Kill Aura", "Attacks the closest player in range", Category.COMBAT);
	}

	public NumberSetting getRange() {
		return range;
	}

	public NumberSetting getCps() {
		return cps;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null || client.world == null || client.interactionManager == null) {
			return;
		}

		long interval = (long) (1000.0 / Math.max(1.0, cps.get()));

		if (System.currentTimeMillis() - lastAttack < interval) {
			return;
		}

		Entity target = findTarget(client);

		if (target == null) {
			return;
		}

		client.interactionManager.attackEntity(client.player, target);

		if (swing.get()) {
			client.player.swingHand(Hand.MAIN_HAND);
		}

		lastAttack = System.currentTimeMillis();
	}

	private Entity findNearest(MinecraftClient client) {
		Entity best = null;
		double bestDistance = range.get();

		for (PlayerEntity player : client.world.getPlayers()) {
			if (player == client.player || !player.isAlive() || player.isRemoved()) {
				continue;
			}

			double distance = player.distanceTo(client.player);

			if (distance <= bestDistance) {
				best = player;
				bestDistance = distance;
			}
		}

		return best;
	}

	private Entity findTarget(MinecraftClient client) {
		return findNearest(client);
	}
}
