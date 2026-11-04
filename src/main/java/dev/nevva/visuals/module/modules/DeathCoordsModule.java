package dev.nevva.visuals.module.modules;

import dev.nevva.visuals.module.Category;
import dev.nevva.visuals.module.Module;
import dev.nevva.visuals.module.setting.BoolSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DeathCoordsModule extends Module {
	private final BoolSetting chat = add(new BoolSetting("death_chat", "Message in chat", true));

	private boolean wasDead;

	public DeathCoordsModule() {
		super("Death Coords", "Remembers the coordinates where you died", Category.MISC);
	}

	public BoolSetting getChat() {
		return chat;
	}

	@Override
	public void onTick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			wasDead = false;
			return;
		}

		boolean dead = client.player.isDead();

		if (dead && !wasDead && chat.get() && client.inGameHud != null) {
			BlockPos pos = client.player.getBlockPos();
			client.inGameHud.getChatHud().addMessage(Text.literal(
					"[NevvaVisuals] Death at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
							+ " in " + client.world.getRegistryKey().getValue()));
		}

		wasDead = dead;
	}
}
