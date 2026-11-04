package dev.nevva.visuals.mixin;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.CustomWorldModule;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the colour the sky is painted with while "Sky colour" is enabled.
 */
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	private void nevva$skyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Integer> cir) {
		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod == null || mod.getModuleManager() == null) {
			return;
		}

		CustomWorldModule module = mod.getModuleManager().get(CustomWorldModule.class);

		if (module == null || !module.isEnabled() || !module.getSkyTint().get()) {
			return;
		}

		cir.setReturnValue(module.getSkyColor().getColor());
	}
}
