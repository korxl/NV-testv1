package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistModule extends Module {
	private final NumberSetting strength = add(new NumberSetting("aim_strength", "Strength %", 35.0, 5.0, 100.0, 5.0));
	private final NumberSetting range = add(new NumberSetting("aim_range", "Range", 4.5, 2.0, 6.0, 0.1));

	public AimAssistModule() {
		super("Aim Assist", "Softly pulls the aim towards the closest player while attacking", Category.COMBAT);
	}

	public NumberSetting getStrength() {
		return strength;
	}

	public NumberSetting getRange() {
		return range;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (!isEnabled() || client.player == null || client.world == null || client.options == null) {
			return;
		}

		if (!client.options.attackKey.isPressed() || client.currentScreen != null) {
			return;
		}

		PlayerEntity target = findTarget(client);

		if (target == null) {
			return;
		}

		Vec3d eye = client.player.getEyePos();
		Vec3d targetPos = target.getPos().add(0.0, target.getHeight() * 0.6, 0.0);
		double dx = targetPos.x - eye.x;
		double dy = targetPos.y - eye.y;
		double dz = targetPos.z - eye.z;
		double horizontal = Math.sqrt(dx * dx + dz * dz);

		float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx))) - 90.0f;
		float targetPitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)));

		float factor = (float) (strength.get() / 100.0);
		float yawDelta = MathHelper.wrapDegrees(targetYaw - client.player.getYaw());
		float pitchDelta = targetPitch - client.player.getPitch();

		client.player.setYaw(client.player.getYaw() + yawDelta * factor);
		client.player.setPitch(MathHelper.clamp(client.player.getPitch() + pitchDelta * factor, -90.0f, 90.0f));
	}

	private PlayerEntity findTarget(MinecraftClient client) {
		PlayerEntity best = null;
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
}
