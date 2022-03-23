# Twitch Vs Minecraft Reloaded (Forge 1.18.2)

[![Build status](https://github.com/iCrazyBlaze/TwitchVsMinecraft2/actions/workflows/build.yml/badge.svg)](https://github.com/iCrazyBlaze/TwitchVsMinecraft2/actions)
[![CurseForge](http://cf.way2muchnoise.eu/full_318840_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)

A Minecraft mod for Forge inspired by [Kaze Emanuar](https://www.youtube.com/channel/UCuvSqzfO_LV_QzHdmEj84SQ)
and [CrowdControl](https://crowdcontrol.live) that lets Twitch viewers interact with the game to provide a fun challenge
for streamers.

[🌐 Project homepage](https://icrazyblaze.github.io/twitchvsminecraft)

[🔨 View the project on CurseForge for more info!](https://www.curseforge.com/minecraft/mc-mods/twitch-vs-minecraft)

[📙 See the documentation if you are making modifications to the code.](https://icrazyblaze.github.io/TwitchVsMinecraft2/)

# Forge version

The latest version of the mod is designed to work with [Forge](https://files.minecraftforge.net) for Minecraft 1.18.2

> This mod is **NOT** backwards compatible with any older Minecraft Forge versions.
> Only versions of the mod for the latest versions of Minecraft Forge will be supported going forwards.

# How it works

## **Using Twitch**

This mod integrates [Twitch4J](https://twitch4j.github.io/), a Java Twitch API wrapper. It uses this library to read a
Twitch channel's chat.

## **Using Discord**

This mod integrates [JDA](https://github.com/DV8FromTheWorld/JDA), a Java Discord API wrapper. It uses JDA to connect to
Discord as a bot, where it will read messages from the channels it can see, and send them through the same system as the
Twitch chat.

## **What the mod does**

Every time a new non-blacklisted chat message is received that starts with the chosen prefix, it is added to a list.
Every 30 seconds (this can be changed), a random message from the list is chosen, and if it's a valid command,
e.g. `"!creeper"`, the list of new chat messages will be cleared, the timer will restart and the command will be
executed. A list of commands is available [here](http://bit.ly/2UfBCiL) and on the website.

# Twitch OAuth key

As stated on CurseForge, you will need a Twitch OAuth key to log in. You can get
this [here.](https://twitchapps.com/tmi)

You should keep this key private and safe. **DO NOT** share this key with others!

Follow the instructions on the TwitchApps page for how to revoke access to the Twitch API if you want to stay extra
safe.

> This key is stored in a file located in the Minecraft root directory under `config`.

# Discord Bot Token

You can also connect a Discord Bot and use your Discord server instead of Twitch chat. Visit
the [Discord Developer Dashboard](https://discord.com/developers/applications), and create a bot.

> This key is stored in a file located in the Minecraft root directory under `mod_data`.

# Editing the configuration file

You will need to edit this file to add your Twitch channel name, change the affected players, or choose what Discord
channels are read from, and you will often use it to change timer settings.

> You should edit this file as soon as you start playing. Make sure you have it on hand while trying the mod as you may need to tweak settings during your game.

Your config file will be located in:

```jsonpath
.minecraft/config/twitchmod-common
```

and will look like this by default when opened in a text editor:

```toml
[general]
	#Name of Twitch channel
	twitch_channel_name = "btarg1"
	#Names of Discord channels to read commands from ['separated', 'like', 'this']
	discord_channels = ["general"]
	#Should chat messages from Twitch or Discord be show in-game?
	show_chat_messages = true
	#Should chosen commands be shown if chat messages are enabled?
	show_commands_in_chat = true
	#How many seconds until the next command is chosen
	#Range: 3 ~ 60
	choose_command_delay = 10
	#How many seconds until a random viewer-written message is shown on screen
	#Range: 10 ~ 480
	choose_message_delay = 100
	#The players' Minecraft usernames that will be effected
	minecraft_username = ["Dev", "Test"]
	#The prefix for commands in Twitch or Discord
	command_prefix = "!"
	#Prevent the same command from being executed twice in a row
	enable_cooldown = true
	#Allow Frenzy Mode
	enable_frenzy = true
	#Require a certain amount of bits for any command
	require_bits = false
	#How many bits are needed to activate commands if they are required
	#Range: > 1
	minimum_bits = 10
	#How many votes are needed to activate special commands (e.g. Frenzy Mode)
	#Range: > 2
	votes_needed = 3
	#How many messages should be included when chat writes a book
	#Range: 5 ~ 99
	book_length = 10
```

# Testing

To test commands without connecting to Twitch or Discord, use `/ttv test [true/false] <string>`.

When connected to Twitch, the Broadcaster's commands are always sent through, bypassing the blacklist and command timer
entirely.

To review settings and view connection status, type `/ttv status`.

# Building from source

> *Before you try building, make sure you [have JDK installed](https://adoptopenjdk.net/) and have properly set up your Java development environment.*

To build the project using a terminal, type

```
./gradlew build
```

Or find it in the Gradle tab in IDEA. Use the `genIntellijRuns` task to set up the mod properly for IDEA.

The final `.jar` mod file will be located in the **build/libs** folder.
