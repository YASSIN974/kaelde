<img src="https://raw.githubusercontent.com/KyokoBot/Kyoko/kyoko-v2/assets/kyokobot-banner2.png" alt="Kyoko"/>

<a href="https://discordbots.org/bot/375750637540868107"><img src="https://discordbots.org/api/widget/upvotes/375750637540868107.svg" alt="Discord Music Bot" /></a> <img src="https://img.shields.io/github/license/KyokoBot/Kyoko.svg"> <img src="https://img.shields.io/github/contributors/KyokoBot/Kyoko.svg"> <img src="https://img.shields.io/badge/jda-3-blue.svg"> [![invite](https://img.shields.io/discord/375752406727786498.svg?logo=discord&colorB=7289DA)](https://discord.gg/ZvDRQf7)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FKyokoBot%2FKyoko.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FKyokoBot%2FKyoko?ref=badge_shield)

Kyoko is an open-source multipurpose Discord bot, mainly for music, fun and moderation. It's developed everyday and new features are constantly added, but if you want - talk to us, suggest something, our Discord server invite is available below.

[Click here to invite bot to server](https://discordapp.com/oauth2/authorize?&client_id=375750637540868107&scope=bot&permissions=2117598326)

### Self-hosting

**Requirements:**

- Java 8+
- RethinkDB 2.3+

**Optional (for full functionality):**

- Lavalink
- API Keys (weeb.sh, YouTube Data API, Sentry DSN, Discord Bot List, Discord Bots, ListCord)

**Installation**

(Pre-built binaries will be available soon)

1. Install Java and RethinkDB.
2. Look below and compile Kyoko from source code.
3. Navigate to `deploy` folder in build root - this folder contains all build artifacts - if you want move it outside.
4. Launch `Kyoko.jar`, it should create configuration files and exit.
5. Edit `config.json` and set your Discord API token (`connection.token`), prefix (`bot.normal-prefix` and `bot.debug-prefix`) and set your ID in `bot.owner` to have access to admin commands.
6. After completed setup start bot by launching `Kyoko.jar` again, and if you want allocate more memory add `-Xmx2G` (=2 GB, set more if you want) to JVM parameters.

### Building/contributing

If you want to contribute to Kyoko [join our server first](https://discord.gg/ZvDRQf7) and read info in contribution-related channels.

**Steps for building**

1. Clone this repo
2. Navigate to project root directory from terminal (location of cloned repo)
3. Run `gradlew assemble` or `./gradlew assemble` on Unix
4. After successful build, the runtime jar and modules will be placed in `deploy` directory.

If you want to run Kyoko from IDE - remember to execute Gradle `assemble` task on every build, set working directory to `<project root>/deploy` (otherwise bot will not respond to any commands because no modules will be loaded) and run it from `Kyoko.jar`. [Look here for example](http://i.imgur.com/EMWG6Ve.png)

### Links

[![Kyoko Discord Bot Support](https://discordapp.com/api/guilds/375752406727786498/embed.png?style=banner3)](https://discord.gg/ZvDRQf7)

[Our community server](https://discord.gg/ZvDRQf7)

[Website](https://kyokobot.moe)

[Commands and features](https://kyokobot.moe/commands)

### Kyoko <3 and uses

[JDA](https://github.com/DV8FromTheWorld/JDA) by [DV8FromTheWorld](https://github.com/DV8FromTheWorld)

[LavaClient](https://github.com/SamOphis/LavaClient) by [SamOphis](https://github.com/SamOphis)

[weeb4j](https://github.com/natanbc/weeb4j) by [natanbc](https://github.com/natanbc)

[RethinkDB](https://rethinkdb.com)

and many other things...

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FKyokoBot%2FKyoko.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FKyokoBot%2FKyoko?ref=badge_large)
