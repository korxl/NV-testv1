package dev.nevva.visuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.nevva.visuals.NevvaVisuals;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path path = FabricLoader.getInstance().getConfigDir().resolve(NevvaVisuals.MOD_ID + ".json");
	private Config config = new Config();

	public Config getConfig() {
		return config;
	}

	public void load() {
		if (!Files.exists(path)) {
			return;
		}

		try {
			String json = Files.readString(path, StandardCharsets.UTF_8);
			Config loaded = GSON.fromJson(json, Config.class);

			if (loaded != null) {
				Config defaults = new Config();
				if (loaded.modules == null) loaded.modules = defaults.modules;
				if (loaded.bools == null) loaded.bools = defaults.bools;
				if (loaded.numbers == null) loaded.numbers = defaults.numbers;
				if (loaded.choices == null) loaded.choices = defaults.choices;
				config = loaded;
			}
		} catch (Exception e) {
			NevvaVisuals.LOGGER.warn("Could not read config {}, keeping defaults", path, e);
		}
	}

	public void save() {
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(config), StandardCharsets.UTF_8);
		} catch (IOException e) {
			NevvaVisuals.LOGGER.warn("Could not write config {}", path, e);
		}
	}
}
