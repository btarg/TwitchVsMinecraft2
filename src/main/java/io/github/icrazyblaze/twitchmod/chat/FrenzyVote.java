package io.github.icrazyblaze.twitchmod.chat;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;

public class FrenzyVote {

    public static ArrayList<String> votedList = new ArrayList<>();
    public static int votes = 0;
    public static int votesNeeded;

    public static void vote(String username) {

        votesNeeded = ConfigManager.VOTES_NEEDED.get();

        if (!votedList.contains(username)) {

            votedList.add(username);
            votes = votedList.toArray().length;

            CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.user_voted_frenzy", username, votes, votesNeeded));

            if (votes == votesNeeded) {
                CommandHandlers.frenzyTimer(10);
                votedList.clear();
            }


        }

    }

}
