package dev.nevva.visuals.mixin;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.CustomFogModule;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
	@Inject(method = "applyFog", at = @At("RETURN"), cancellable = true)
	private static void nevva$customFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color,
										float viewDistance, boolean thickenFog, float tickDelta,
										CallbackInfoReturnable<Fog> cir) {
		CustomFogModule module = resolve();

		if (module == null) {
			return;
		}

		Fog original = cir.getReturnValue();

		if (original == null) {
			return;
		}

		float start = (float) module.getFogStart().get();
		float end = (float) module.getFogEnd().get();

		if (end <= start + 1.0f) {
			end = start + 1.0f;
		}

		cir.setReturnValue(new Fog(start, end, original.shape(), original.red(), original.green(), original.blue(), original.alpha()));
	}

	@Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
	private static void nevva$fogColor(Camera camera, float tickDelta, ClientWorld world, int clampedViewDistance,
									   float skyDarkness, CallbackInfoReturnable<Vector4f> cir) {
		CustomFogModule module = resolve();

		if (module == null || !module.getTint().get()) {
			return;
		}

		int color = module.getColor().getColor();
		float red = ((color >> 16) & 0xFF) / 255.0f;
		float green = ((color >> 8) & 0xFF) / 255.0f;
		float blue = (color & 0xFF) / 255.0f;

		Vector4f original = cir.getReturnValue();

		if (original == null) {
			cir.setReturnValue(new Vector4f(red, green, blue, 1.0f));
			return;
		}

		cir.setReturnValue(new Vector4f(red, green, blue, original.w()));
	}

	private static CustomFogModule resolve() {
		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod == null || mod.getModuleManager() == null) {
			return null;
		}

		CustomFogModule module = mod.getModuleManager().get(CustomFogModule.class);

		return module != null && module.isEnabled() ? module : null;
	}
}
