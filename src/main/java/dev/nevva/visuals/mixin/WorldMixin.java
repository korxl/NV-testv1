package dev.nevva.visuals.mixin;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.CustomWorldModule;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side world tweaks: a custom time of day and (optionally) clear weather.
 * Only the client world is touched, the server still runs on its own time.
 */
@Mixin(World.class)
public class WorldMixin {
	@Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
	private void nevva$timeOfDay(CallbackInfoReturnable<Long> cir) {
		CustomWorldModule module = nevva$resolve();

		if (module == null || !module.getCustomTime().get()) {
			return;
		}

		cir.setReturnValue((long) module.getWorldTime().get());
	}

	@Inject(method = "getSkyAngleRadians", at = @At("HEAD"), cancellable = true)
	private void nevva$skyAngle(float tickDelta, CallbackInfoReturnable<Float> cir) {
		CustomWorldModule module = nevva$resolve();

		if (module == null || !module.getCustomTime().get()) {
			return;
		}

		double dayProgress = (module.getWorldTime().get() % 24000.0) / 24000.0 - 0.25;
		double skyAngle = 0.5 - Math.cos(dayProgress * Math.PI) / 2.0;

		cir.setReturnValue((float) (skyAngle * Math.PI * 2.0));
	}

	@Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
	private void nevva$rainGradient(float delta, CallbackInfoReturnable<Float> cir) {
		CustomWorldModule module = nevva$resolve();

		if (module != null && module.getClearWeather().get()) {
			cir.setReturnValue(0.0f);
		}
	}

	@Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
	private void nevva$thunderGradient(float delta, CallbackInfoReturnable<Float> cir) {
		CustomWorldModule module = nevva$resolve();

		if (module != null && module.getClearWeather().get()) {
			cir.setReturnValue(0.0f);
		}
	}

	private CustomWorldModule nevva$resolve() {
		World self = (World) (Object) this;

		if (!self.isClient()) {
			return null;
		}

		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod == null || mod.getModuleManager() == null) {
			return null;
		}

		CustomWorldModule module = mod.getModuleManager().get(CustomWorldModule.class);

		return module != null && module.isEnabled() ? module : null;
	}
}
