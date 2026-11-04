package dev.nevva.visuals;

import dev.nevva.visuals.config.ConfigManager;
import dev.nevva.visuals.gui.NevvaScreen;
import dev.nevva.visuals.hud.HudRenderer;
import dev.nevva.visuals.hud.Keystrokes;
import dev.nevva.visuals.hud.Notifications;
import dev.nevva.visuals.hud.TargetHud;
import dev.nevva.visuals.hud.Watermark;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.ModuleManager;
import dev.nevva.visuals.module.modules.HudModule;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NevvaVisuals implements ClientModInitializer {
	public static final String MOD_ID = "nevvavisuals";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static NevvaVisuals instance;

	private final ConfigManager configManager = new ConfigManager();
	private ModuleManager moduleManager;
	private HudRenderer hudRenderer;
	private TargetHud targetHud;
	private Keystrokes keystrokes;
	private Watermark watermark;
	private Notifications notifications;
	private boolean loaded;

	public static NevvaVisuals getInstance() {
		return instance;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		Keybinds.register();
		configManager.load();

		moduleManager = new ModuleManager();
		moduleManager.applyConfig(configManager.getConfig());

		hudRenderer = new HudRenderer(this);
		targetHud = new TargetHud(this);
		keystrokes = new Keystrokes(this);
		watermark = new Watermark(this);
		notifications = new Notifications(this);

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveConfig());
		HudRenderCallback.EVENT.register((context, tickCounter) -> renderHud(context));

		loaded = true;
		LOGGER.info("NevvaVisuals loaded");
	}

	private void onClientTick(MinecraftClient client) {
		if (Keybinds.MENU != null) {
			while (Keybinds.MENU.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new NevvaScreen(this));
				}
			}
		}

		if (Keybinds.HUD != null) {
			while (Keybinds.HUD.wasPressed()) {
				HudModule hud = moduleManager.get(HudModule.class);

				if (hud != null) {
					hud.toggle();
				}
			}
		}

		moduleManager.onTick(client);
		watermark.tick(client);
		hudRenderer.tick(client);
		targetHud.tick(client);
		keystrokes.tick(client);
		notifications.tick(client);
	}

	private void renderHud(DrawContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.world == null || client.options == null) {
			return;
		}

		watermark.render(context);
		hudRenderer.render(context);
		targetHud.render(context);
		keystrokes.render(context);
		notifications.render(context);
	}

	public void onModuleToggled(Module module, boolean enabled, boolean persist) {
		if (!persist) {
			return;
		}

		saveConfig();

		if (notifications != null && configManager.getConfig().notifications) {
			notifications.push(module.getName(), enabled);
		}
	}

	public void saveConfig() {
		ModuleManager modules = this.moduleManager;

		if (!loaded || modules == null) {
			return;
		}

		modules.writeTo(configManager.getConfig());
		configManager.save();
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public Notifications getNotifications() {
		return notifications;
	}
}
