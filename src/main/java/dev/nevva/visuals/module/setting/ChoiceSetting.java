package dev.nevva.visuals.module.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ChoiceSetting extends Setting<String> {
	private final List<String> values;
	private int index;

	public ChoiceSetting(String key, String label, String defaultValue, String... values) {
		super(key, label);
		this.values = new ArrayList<>(Arrays.asList(values));
		int found = this.values.indexOf(defaultValue);
		this.index = found < 0 ? 0 : found;
	}

	@Override
	public String getValue() {
		return values.get(index);
	}

	public List<String> getValues() {
		return values;
	}

	public int getIndex() {
		return index;
	}

	public void setValue(String value) {
		int found = values.indexOf(value);

		if (found >= 0) {
			this.index = found;
		}
	}

	public void cycle(int direction) {
		if (values.isEmpty()) {
			return;
		}

		int size = values.size();
		this.index = ((index + direction) % size + size) % size;
	}
}
