package io.github.icrazyblaze.twitchmod.util.timers;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TimerSystem {

    public static boolean enableTimers = true;
    public static int chatSecondsTrigger = 30;
    public static int chatSeconds = chatSecondsTrigger;
    public static int deathTimerSeconds = 60;
    public static boolean deathTimerEnabled = false;
    public static int frenzyTimerSeconds = 10;
    public static int peaceTimerSeconds = 30;
    public static boolean peaceTimerEnabled = false;
    public static int messageSecondsTrigger = 240;
    public static int messageSeconds = messageSecondsTrigger;
    static int ticks = 0;

    @SubscribeEvent
    public static void tickTimer(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END && enableTimers) {

            ticks++;

            if (ticks == 20) { // 20 serverticks = 1 second
                second();
                ticks = 0;
            }

        }
    }

    public static void second() {

        if (chatSeconds > 0) {
            chatSeconds--; // Seconds left decreases by 1
        } else if (chatSeconds == 0) {

            ChatPicker.pickRandomChat();

            // Reset timer
            chatSeconds = chatSecondsTrigger;

        }

        // Death timer
        if (deathTimerSeconds > 0) {
            deathTimerSeconds--;
        } else if (deathTimerSeconds == 0) {

            PlayerHelper.player().onKillCommand();
            deathTimerEnabled = false;

        }

        // Frenzy mode timer
        if (ChatPicker.instantCommands) {

            if (frenzyTimerSeconds > 0) {
                frenzyTimerSeconds--;
            } else if (frenzyTimerSeconds == 0) {
                CommandHandlers.disableFrenzyTimer();
            }
        }

        // Peace timer
        if (peaceTimerEnabled) {
            if (peaceTimerSeconds > 0) {
                peaceTimerSeconds--;
            } else if (peaceTimerSeconds == 0) {
                CommandHandlers.disableGraceTimer();
            }

        }

        // Choosing random messages to display in chat
        if (messageSeconds < messageSecondsTrigger) {
            messageSeconds++;
        } else if (messageSeconds == messageSecondsTrigger) {

            CommandHandlers.chooseRandomMessage();
            messageSeconds = 0;

        }
    }
}
