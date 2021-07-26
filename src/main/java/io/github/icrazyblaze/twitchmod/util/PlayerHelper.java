package io.github.icrazyblaze.twitchmod.util;


import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.List;

public class PlayerHelper {

    public static MinecraftServer defaultServer = null;
    public static List<? extends String> affectedPlayers;
    private static String username = null;

    /**
     * This method gets a reference to the player, using the username specified. If the player is not found, it will get the first player in the list.
     *
     * @return player
     */
    public static ServerPlayer player() {

        PlayerList playerList = defaultServer.getPlayerList();
        ServerPlayer player = playerList.getPlayerByName(getUsername());

        if (player == null) {
            player = getDefaultPlayer();
        }

        return player;

    }

    private static ServerPlayer getDefaultPlayer() {

        PlayerList playerList = defaultServer.getPlayerList();

        if (playerList.getPlayerCount() == 0) {
            return null;
        }

        return playerList.getPlayers().get(0);

    }

    public static String getUsername() {

        try {
            if (username.isEmpty()) {
                username = getDefaultPlayer().getName().getString();
            }

            return username;

        } catch (Exception e) {
            return "";
        }

    }

    public static void setUsername(String newname) {
        username = newname;
    }
}
