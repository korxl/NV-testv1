package dev.nevva.visuals.hud;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

public class ArrayListModule extends Module {
	private final ChoiceSetting side = add(new ChoiceSetting("arraylist_side", "Side", "RIGHT",
			"RIGHT", "LEFT"));
	private final BoolSetting bar = add(new BoolSetting("arraylist_bar", "Indicator bar", true));
	private final BoolSetting gradient = add(new BoolSetting("arraylist_gradient", "Gradient", true));
	private final NumberSetting scale = add(new NumberSetting("arraylist_scale", "Scale %", 100.0, 60.0, 160.0, 5.0));

	public ArrayListModule() {
		super("ArrayList", "List of enabled modules", Category.HUD);
	}

	public ChoiceSetting getSide() {
		return side;
	}

	public BoolSetting getBar() {
		return bar;
	}

	public BoolSetting getGradient() {
		return gradient;
	}

	public NumberSetting getScale() {
		return scale;
	}

	public boolean isRight() {
		return "RIGHT".equals(side.getValue());
	}
}
