package com.icrazyblaze.twitchmod.util;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickHandler {

    public static boolean enableTimers = true;

    public static int chatTicks = 0;
    public static int chatSecondsTrigger = 30;
    public static int chatSeconds = chatSecondsTrigger;

    public static int deathTimerTicks = 0;
    public static int deathTimerSeconds = 60;
    public static boolean deathTimer = false;

    public static int peaceTimerTicks = 0;
    public static int peaceTimerSeconds = 30;
    public static boolean peaceTimer = false;

    public static int messageTicks = 0;
    public static int messageSecondsTrigger = 240;
    public static int messageSeconds = messageSecondsTrigger;

    /**
     * This method is used for timers such as the death timer as thread.sleep cannot be called while playing.
     * Countdown timers look like this:
     * <pre>
     * {@code
     *     if (condition) {
     *
     *                 ticks++;
     *
     *                 if (ticks == 20) {
     *
     *                     if (seconds > 0) {
     *                         seconds--;
     *                     }
     *
     *                     ticks = 0;
     *
     *                     if (seconds == 0) {
     *                         // do something
     *                         seconds = defaultAmountOfSeconds;
     *                         ticks = 0;
     *                     }
     *
     *                 }
     *
     *             }
     * }
     * </pre>
     * Timers are set and reset externally by changing the condition.
     * The example can also be configured to count up.
     * 20 serverticks is equal to one second realtime.
     *
     * @param event Ignore this
     */
    @SubscribeEvent
    public static void tickTimer(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END && enableTimers) {

            chatTicks++;

            if (chatTicks == 20) { // 20 serverticks = 1 second

                if (chatSeconds > 0) {
                    chatSeconds--; // Seconds left decreases by 1
                }

                chatTicks = 0;
            }
            if (chatSeconds == 0) {

                ChatPicker.pickRandomChat();

                // Reset timer
                chatSeconds = chatSecondsTrigger;
                chatTicks = 0;

            }

            if (deathTimer) {

                deathTimerTicks++;

                if (deathTimerTicks == 20) {

                    if (deathTimerSeconds > 0) {
                        deathTimerSeconds--;
                    }

                    deathTimerTicks = 0;

                }
                if (deathTimerSeconds == 0) {

                    BotCommands.killPlayer();
                    deathTimer = false;

                }
            }

            if (peaceTimer) {

                peaceTimerTicks++;

                if (peaceTimerTicks == 20) {

                    if (peaceTimerSeconds > 0) {
                        peaceTimerSeconds--;
                    }

                    peaceTimerTicks = 0;

                }
                if (peaceTimerSeconds == 0) {

                    BotCommands.disableGraceTimer();
                    peaceTimer = false;

                }
            }


            messageTicks++;

            if (messageTicks == 20) {

                if (messageSeconds < messageSecondsTrigger) {
                    messageSeconds++;
                }

                messageTicks = 0;

            }
            if (messageSeconds == messageSecondsTrigger) {

                BotCommands.chooseRandomMessage();

                messageTicks = 0;
                messageSeconds = 0;

            }
        }
    }

}