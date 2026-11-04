package dev.nevva.visuals.module.setting;

public final class BoolSetting extends Setting<Boolean> {
	private boolean value;

	public BoolSetting(String key, String label, boolean defaultValue) {
		super(key, label);
		this.value = defaultValue;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	public void toggle() {
		this.value = !this.value;
	}
}
