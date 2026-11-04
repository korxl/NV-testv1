package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.Keybinds;
import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class ZoomModule extends Module {
	private final NumberSetting zoomFov = add(new NumberSetting("zoom_fov", "Zoom FOV", 30.0, 10.0, 110.0, 1.0));

	private int savedFov = -1;

	public ZoomModule() {
		super("Zoom", "Hold C to zoom in", Category.RENDER);
		setEnabled(true, false);
	}

	public NumberSetting getZoomFov() {
		return zoomFov;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (client.options == null) {
			return;
		}

		SimpleOption<Integer> fov = client.options.getFov();
		boolean active = isEnabled()
				&& Keybinds.ZOOM != null
				&& Keybinds.ZOOM.isPressed()
				&& client.currentScreen == null;

		if (active) {
			if (savedFov < 0) {
				savedFov = fov.getValue();
				fov.setValue((int) Math.round(zoomFov.get()));
			}
		} else if (savedFov >= 0) {
			fov.setValue(savedFov);
			savedFov = -1;
		}
	}

	public static ZoomModule get() {
		NevvaVisuals mod = NevvaVisuals.getInstance();

		return mod == null || mod.getModuleManager() == null ? null : mod.getModuleManager().get(ZoomModule.class);
	}
}
