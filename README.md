# holy-core

[![Status: Archived](https://img.shields.io/badge/status-archived-lightgrey)](#repository-status)
[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License](https://img.shields.io/github/license/teamholy-network/holy-core)](LICENSE)

The archived source code of the former TeamHoly Minecraft network core.

> [!WARNING]
> This repository is archived and preserved for historical reference only. It is no longer maintained, supported, or updated. There will be no new features, releases, security fixes, or compatibility updates. Do not use this project in production.

## Repository status

`holy-core` represents the final state of a network-specific legacy system. The repository remains available so former contributors and interested developers can inspect the implementation and history of the project.

- The repository is read-only and receives no further pushes.
- Issues, feature requests, and pull requests are not processed.
- No support or setup assistance is provided.
- Published artifacts are historical and will not receive updates.
- Forks are independent projects and are not supported by TeamHoly.

This code should not be treated as a current reference architecture or as a ready-to-run Minecraft plugin suite.

## What this project contained

The project combined shared backend services and platform integrations for the TeamHoly Minecraft network, including:

- Player profiles, ranks, currencies, online time, skins, and settings
- Friends, parties, clans, invitations, and private messaging
- Bans, mutes, reports, chat logs, staff tools, and chat filtering
- Game statistics and Redis-backed leaderboards
- Bukkit utilities for scoreboards, NPCs, perks, nicknames, and skins
- BungeeCord commands, listeners, player lifecycle, and network messaging
- Premium and offline-mode account login flows
- CloudNet health checks and scheduled statistics resets
- MongoDB persistence with Redis caching and distributed state

## Archived modules

| Module | Original role |
| --- | --- |
| `holy-core-api` | Shared models, MongoDB repositories, Redis services, ranks, statistics, social features, and CloudNet integration |
| `bukkit-core-api` | Bukkit/Spigot commands, listeners, NPCs, perks, scoreboards, and server health reporting |
| `bungee-core-api` | Proxy commands, moderation, chat, friends, parties, clans, player lifecycle, and messaging |
| `bungee-login` | Registration, login, premium-account handling, bot protection, and IP checks |
| `bukkit-markupapi` | Nicknames, skins, presets, name tags, and packet-based profile updates |
| `holy-core-cloudmodule` | CloudNet health monitoring, ranking maintenance, and statistics resets |

## Historical technology stack

These versions describe the final checked-in build and are not recommendations or compatibility guarantees.

| Component | Final repository target |
| --- | --- |
| Java | 17 |
| Gradle Wrapper | 8.8 |
| Project artifact version | 2.9.0 |
| Spigot | 1.8.8 |
| BungeeCord API | 1.20-R0.2 |
| CloudNet | 3.4.0-RELEASE |
| LuckPerms API | 5.4 |
| ProtocolLib | 5.x |
| Redisson | 3.19.1 |
| MongoDB BSON driver | 4.10.2 |

The runtime originally also depended on MongoDB, Redis, LuckPerms, CloudNet Bridge, ProtocolLib, TeamHoly web services, and other network-specific infrastructure that is not included in this repository.

## Building the archived source

The source can still be compiled for inspection or preservation. A successful build does not mean that the resulting artifacts are safe or functional in a modern environment.

Requirements:

- JDK 17
- Git, because build metadata is generated from the current commit
- Internet access for the historical dependencies

Linux and macOS:

```bash
git clone https://github.com/teamholy-network/holy-core.git
cd holy-core
./gradlew clean build
./gradlew shadowJar
```

Windows:

```powershell
git clone https://github.com/teamholy-network/holy-core.git
cd holy-core
.\gradlew.bat clean build
.\gradlew.bat shadowJar
```

Shaded JARs are copied to the root-level `output/` directory. Standard JARs remain in each module's `build/libs/` directory.

There are no automated test sources in the archived repository. The Gradle build only confirms that the final source state compiles against its configured dependencies.

## Historical configuration assumptions

The implementation was tightly coupled to the former TeamHoly infrastructure. Among other assumptions, it expects:

- A shared configuration at `/home/Cloud/mongodb.cfg`
- MongoDB for persistent profile and domain data
- A password-protected Redis instance at `127.0.0.1:6379`
- CloudNet services and network-specific service names
- LuckPerms and ProtocolLib on the relevant platforms
- TeamHoly-specific websites, APIs, webhooks, and server names
- A particular multi-plugin class-loading and deployment layout

These assumptions were never generalized for third-party installations. No complete deployment guide is provided because the required original infrastructure is no longer part of the project.

## Security notice

The archived code has known security and operational weaknesses. It must not be deployed without a complete review and substantial changes.

- Historical API keys and Discord webhook URLs were committed directly in source files. They must be considered compromised.
- Removing a credential from the latest source does not remove it from Git history.
- Database settings, service URLs, filesystem paths, and network names are hard-coded or tied to one environment.
- The offline-mode login implementation uses outdated password handling and requires a full security redesign.
- Some Redis and CloudNet messages can trigger command execution and require strict authentication and authorization controls.
- Redis is used for state and persistence patterns that are unsafe for a modern distributed system.
- Dependencies and Minecraft platform APIs are old and may contain unpatched vulnerabilities or incompatibilities.
- Runtime descriptor versions do not consistently match the Gradle artifact version.

Do not report vulnerabilities expecting a patch: this project has reached end of life. If you fork the repository, you assume responsibility for replacing credentials, reviewing the full Git history, updating dependencies, and securing the resulting system.

## Third-party code

Some source files retain separate third-party copyright or license notices. Those notices continue to apply. Anyone redistributing a fork is responsible for reviewing the source and dependency licenses and preserving all required notices.

## License

The repository is provided under the [Apache License 2.0](LICENSE), subject to any additional notices contained in individual files and third-party dependencies.
