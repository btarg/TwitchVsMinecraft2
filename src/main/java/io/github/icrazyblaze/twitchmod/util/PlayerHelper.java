package io.github.icrazyblaze.twitchmod.util;

import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;

public class PlayerHelper {

    public static MinecraftServer defaultServer = null;

    /**
     * This method gets a reference to the player, using the username specified. If the player is not found, it will get the first player in the list.
     *
     * @return player
     */
    public static ServerPlayerEntity player() {

        PlayerList playerList = defaultServer.getPlayerList();
        ServerPlayerEntity player = playerList.getPlayerByUsername(BotConfig.getUsername());

        if (player == null) {
            player = PlayerHelper.getDefaultPlayer();
        }

        return player;

    }

    public static ServerPlayerEntity getDefaultPlayer() {

        PlayerList playerList = defaultServer.getPlayerList();
        return playerList.getPlayers().get(0);

    }
}
