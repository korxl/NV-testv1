package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.ChoiceSetting;
import dev.nevva.visuals.module.setting.ColorSetting;
import dev.nevva.visuals.ui.Theme;

public class InterfaceModule extends Module {
	private final ChoiceSetting theme = add(new ChoiceSetting("theme", "Theme", "Nursultan",
			"Nursultan", "Sadness", "Rockstar", "Delta", "Wex", "Custom"));
	private final ColorSetting accent = add(new ColorSetting("accent_hue", "Custom accent", 190.0f));
	private final ChoiceSetting font = add(new ChoiceSetting("font", "Font", "Default",
			"Default", "Caps", "Soft"));
	private final BoolSetting notifications = add(new BoolSetting("notifications", "Notifications", true));

	public InterfaceModule() {
		super("Interface", "Theme, accent colour, font and notifications", Category.CLIENT);
		setEnabled(true, false);
	}

	public ChoiceSetting getTheme() {
		return theme;
	}

	public ColorSetting getAccent() {
		return accent;
	}

	public ChoiceSetting getFont() {
		return font;
	}

	public BoolSetting getNotifications() {
		return notifications;
	}

	public Theme resolveTheme() {
		return Theme.of(theme.getValue(), accent.getHue());
	}
}
