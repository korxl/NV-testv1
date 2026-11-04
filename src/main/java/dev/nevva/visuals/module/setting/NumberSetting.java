package dev.nevva.visuals.module.setting;

public final class NumberSetting extends Setting<Double> {
	private final double min;
	private final double max;
	private final double step;
	private double value;

	public NumberSetting(String key, String label, double defaultValue, double min, double max, double step) {
		super(key, label);
		this.min = min;
		this.max = max;
		this.step = step <= 0.0 ? 0.01 : step;
		this.value = clamp(defaultValue);
	}

	@Override
	public Double getValue() {
		return value;
	}

	public double get() {
		return value;
	}

	public void set(double newValue) {
		this.value = clamp(newValue);
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getStep() {
		return step;
	}

	/** Maps a 0..1 slider progress to a snapped value. */
	public void setProgress(double progress) {
		double raw = min + (max - min) * Math.max(0.0, Math.min(1.0, progress));
		set(raw);
	}

	public double getProgress() {
		return (value - min) / (max - min);
	}

	private double clamp(double raw) {
		double clamped = Math.max(min, Math.min(max, raw));
		double snapped = min + Math.round((clamped - min) / step) * step;
		return Math.max(min, Math.min(max, snapped));
	}
}
