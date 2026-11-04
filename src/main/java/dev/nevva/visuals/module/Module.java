package dev.nevva.visuals.module;

import dev.nevva.visuals.NevvaVisuals;
import dev.nevva.visuals.module.setting.Setting;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
	private final String name;
	private final String description;
	private final Category category;
	private final List<Setting<?>> settings = new ArrayList<>();
	private boolean enabled;
	private int keyCode = -1;

	protected Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public List<Setting<?>> getSettings() {
		return settings;
	}

	protected <T extends Setting<?>> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode < 0 ? -1 : keyCode;
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public void setEnabled(boolean enabled) {
		setEnabled(enabled, true);
	}

	public void setEnabled(boolean enabled, boolean persist) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;

		if (enabled) {
			onEnable();
		} else {
			onDisable();
		}

		NevvaVisuals mod = NevvaVisuals.getInstance();

		if (mod != null) {
			mod.onModuleToggled(this, enabled, persist);
		}
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void onTick(MinecraftClient client) {
	}
}
