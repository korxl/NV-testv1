package dev.nevva.visuals.anim;

/**
 * Frame-rate independent smoothing used by the menu and the HUD animations.
 */
public final class Animation {
	private final double speed;
	private double value;
	private double target;
	private long lastUpdate;

	public Animation(double initial, double speed) {
		this.value = initial;
		this.target = initial;
		this.speed = speed;
		this.lastUpdate = System.nanoTime();
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public void setValue(double value) {
		this.value = value;
		this.target = value;
	}

	public double get() {
		return value;
	}

	public void update() {
		long now = System.nanoTime();
		double delta = Math.min(0.2, (now - lastUpdate) / 1.0e9);
		lastUpdate = now;

		value += (target - value) * (1.0 - Math.exp(-speed * delta));

		if (Math.abs(target - value) < 0.001) {
			value = target;
		}
	}
}
