package io.github.icrazyblaze.twitchmod.chat;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;

public class FrenzyVote {

    public static ArrayList<String> votedList = new ArrayList<>();
    public static int votes = 0;
    public static int votesNeeded = 3;

    public static void vote(String username) {

        if (!votedList.contains(username)) {

            votedList.add(username);
            votes = votedList.toArray().length;

            CommandHandlers.broadcastMessage(new TextComponent(username + String.format(" wants to enable Frenzy Mode. (%s/%s)", votes, votesNeeded)));

            if (votes == votesNeeded) {
                CommandHandlers.frenzyTimer();
                votedList.clear();
            }


        }

    }

}
