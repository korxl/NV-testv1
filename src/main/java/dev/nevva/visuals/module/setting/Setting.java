package dev.nevva.visuals.module.setting;

/**
 * A single value of a module that is stored in the config file.
 */
public abstract class Setting<T> {
	private final String key;
	private final String label;

	protected Setting(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/** Unique key used in the config file. */
	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public abstract T getValue();
}
