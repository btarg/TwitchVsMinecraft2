# Twitch Vs Minecraft Reloaded (Now available for 1.16!)
[![forthebadge](https://forthebadge.com/images/badges/fuck-it-ship-it.svg)](https://forthebadge.com)
[![Build status](https://ci.appveyor.com/api/projects/status/xoql77ww8lpbpmyo?svg=true)](https://ci.appveyor.com/project/iCrazyBlaze/twitchvsminecraft2)
[![CurseForge](http://cf.way2muchnoise.eu/full_twitch-vs-minecraft_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)

A Minecraft mod for Forge inspired by [Kaze Emanuar](https://www.youtube.com/channel/UCuvSqzfO_LV_QzHdmEj84SQ) and [CrowdControl](https://crowdcontrol.live) that lets Twitch viewers interact with the game to provide a fun challenge for streamers.

[View the project on CurseForge for more info!](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)

[See the documentation if you are making modifications to the code.](https://icrazyblaze.github.io/TwitchVsMinecraft2/)


# Forge version
This version of the mod is designed to work with Minecraft Forge 1.16.3 and above.

# How it works
## **Twitch**
This mod integrates [PircBotX](https://github.com/pircbotx/pircbotx), a Java IRC API. It uses PircBotX to connect to Twitch's IRC server and read a Twitch channel's chat.
## **Discord**
This mod integrates [JDA](https://github.com/DV8FromTheWorld/JDA), a Java Discord API wrapper. It uses JDA to connect to Discord as a bot, where it will read messages from the channels it can see, and send them through the same system as the Twitch chat.
## **What it does**
Every time a new chat message is recieved that isn't blacklisted and starts with the chosen prefix, it is added to a list. Every 30 seconds (this can be changed), a random message from the list is chosen, and if it's a valid command, e.g. `"!creeper"`, the list of new chat messages will be cleared, the timer will restart and the command will be executed. A list of commands is available on the CurseForge page.

# Twitch OAuth key
As stated on CurseForge, you will need a Twitch OAuth key. You can get this [here.](https://twitchapps.com/tmi)

You should keep this key private and safe. **DO NOT** share this key with others!

Follow the instructions on the TwitchApps page for how to revoke access to the Twitch API if you want to stay extra safe.

> This key needs to be reset every time the game is restarted using the in-game command `/ttv key`.

# Discord Bot Token
As of version `3.1.0`, you can connect a Discord Bot and use your Discord server instead of Twitch chat.
Visit the [Discord Developer Dashboard](https://discord.com/developers/applications), and create a bot.

> This token needs to be reset every time the game is restarted using the in-game command `/discord token`.

# Building from source
> *Before you try building,m ake sure you [have JDK installed](https://adoptopenjdk.net/) and have properly set up your Java development environment.*

To build the project using a terminal, type
```
./gradlew build
```
Or find it in the Gradle tab in IDEA.

The build will be located in the **build/libs** folder.