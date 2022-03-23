package io.github.icrazyblaze.twitchmod.util.timers;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/**
 * This class is used for countdown timers such as the Death Timer as thread.sleep cannot be called while playing.
 * Timers are set and reset externally by changing their respective condition.
 *
 * @see io.github.icrazyblaze.twitchmod.CommandHandlers
 */
public class TimerSystem {

    public static boolean enableTimers = true;
    public static ForgeConfigSpec.IntValue chatSecondsTrigger = ConfigManager.CHOOSE_COMMAND_DELAY;
    public static int chatSeconds = chatSecondsTrigger.get();
    public static int deathTimerSeconds = 60;
    public static boolean deathTimerEnabled = false;
    public static int frenzyTimerSeconds = 10;
    public static int peaceTimerSeconds = 30;
    public static boolean peaceTimerEnabled = false;
    public static ForgeConfigSpec.IntValue messageSecondsTrigger = ConfigManager.CHOOSE_MESSAGE_DELAY;
    public static int messageSeconds = messageSecondsTrigger.get();
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
            chatSeconds = chatSecondsTrigger.get();

        }

        // Death timer
        if (deathTimerEnabled) {
            if (deathTimerSeconds > 0) {
                deathTimerSeconds--;
            } else if (deathTimerSeconds == 0) {
                PlayerHelper.player().kill();
                deathTimerEnabled = false;
            }
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
        if (messageSeconds < messageSecondsTrigger.get()) {
            messageSeconds++;
        } else if (messageSeconds == messageSecondsTrigger.get()) {

            CommandHandlers.chooseRandomMessage();
            messageSeconds = 0;

        }
    }
}
