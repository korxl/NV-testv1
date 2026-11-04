package dev.nevva.visuals.mixin;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.modules.AspectRatioModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "getBasicProjectionMatrix", at = @At("RETURN"), cancellable = true)
	private void nevva$aspectRatio(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod == null || mod.getModuleManager() == null) {
			return;
		}

		AspectRatioModule module = mod.getModuleManager().get(AspectRatioModule.class);

		if (module == null || !module.isEnabled()) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();

		if (client.getWindow() == null) {
			return;
		}

		int framebufferWidth = client.getWindow().getFramebufferWidth();
		int framebufferHeight = client.getWindow().getFramebufferHeight();

		if (framebufferWidth <= 0 || framebufferHeight <= 0) {
			return;
		}

		float current = (float) framebufferWidth / (float) framebufferHeight;
		float custom = (float) module.getAspect().get();

		if (custom <= 0.1f) {
			return;
		}

		Matrix4f matrix = cir.getReturnValue();
		matrix.m00(matrix.m00() * (current / custom));
	}
}
