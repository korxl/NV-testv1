package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;
import dev.nevva.visuals.module.setting.NumberSetting;

/** UI-only music card state; it does not play or modify audio. */
public final class MusicWidgetModule extends Module {
	private final BoolSetting playing = add(new BoolSetting("music_playing", "Playback preview", false));
	private final NumberSetting progress = add(new NumberSetting("music_progress", "Track progress", 42.0, 0.0, 100.0, 1.0));
	private final NumberSetting bassBoost = add(new NumberSetting("music_bass_boost", "BassBoost", 35.0, 0.0, 100.0, 1.0));

	public MusicWidgetModule() {
		super("Music Widget", "Track card and audio controls", Category.RENDER);
		setEnabled(true, false);
	}

	public BoolSetting getPlaying() {
		return playing;
	}

	public NumberSetting getProgress() {
		return progress;
	}

	public NumberSetting getBassBoost() {
		return bassBoost;
	}
}
