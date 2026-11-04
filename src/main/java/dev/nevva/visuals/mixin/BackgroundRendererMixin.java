package dev.nevva.visuals.mixin;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.CustomFogModule;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;

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
		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod == null || mod.getModuleManager() == null) {
			return;
		}

		CustomFogModule module = mod.getModuleManager().get(CustomFogModule.class);

		if (module == null || !module.isEnabled()) {
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
}
