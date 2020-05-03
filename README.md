# Twitch Vs Minecraft Reloaded
[![Build status](https://ci.appveyor.com/api/projects/status/xoql77ww8lpbpmyo?svg=true)](https://ci.appveyor.com/project/iCrazyBlaze/twitchvsminecraft2)
[![CurseForge](http://cf.way2muchnoise.eu/full_twitch-vs-minecraft_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)

A Minecraft mod for Forge inspired by [Kaze Emanuar](https://www.youtube.com/channel/UCuvSqzfO_LV_QzHdmEj84SQ) and [CrowdControl](https://crowdcontrol.live) that lets Twitch viewers interact with the game to provide a fun challenge for streamers.

[View the project on CurseForge for more info!](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)


# Codebase Rewrite
This version is being re-written to work with Minecraft 1.15.2 and above. A port to 1.14.4 is currently not in the works, however, once the mod is in a stable state it may be considered. The 1.12.2 version of the mod will be discontinued shortly after the new version's release.

# How it works
This mod integrates [PircBotX](https://github.com/pircbotx/pircbotx), a Java IRC API. It uses PircBotX to connect to Twitch's IRC server and read a Twitch channel's chat. Every time a new chat message is recieved that isn't blacklisted and starts with the chosen prefix, it is added to a list. Every 30 seconds (this can be changed), a random message from the list is chosen, and if it's a valid command, e.g. "!creeper", the list of new chat messages will be cleared, the timer will restart and the command will be executed. A list of commands is available on the CurseForge page.

# Twitch OAuth key
As stated on CurseForge, you will need a Twitch OAuth key. You can get this [here.](https://twitchapps.com/tmi)

You should keep this key private and safe. **DO NOT** share this key with others!

Follow the instructions on the TwitchApps page for how to revoke access to the Twitch API if you want to stay extra safe.

This key needs to be reset every time the game is restarted using the in-game command `/ttv key`.

# Building from source
To build the project using a terminal, type
```
./gradlew build
```
Or find it in the Gradle tab in IDEA.

The build will be located in the **build/libs** folder, alongside the "sources" file. **The sources file is not a mod!**
