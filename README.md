<img src="img/wordmark.png" alt="Szlaban Word mark logo" />

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
  <h1>Szlaban</h1>
</div>

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/PiotrWey/Szlaban/ci.yml?style=plastic&logo=github)](https://github.com/PiotrWey/Szlaban/actions/workflows/ci.yml)
[![Codecov](https://img.shields.io/codecov/c/github/PiotrWey/Szlaban?style=plastic&logo=codecov)](https://app.codecov.io/github/PiotrWey/Szlaban)
[![bStats Servers](https://img.shields.io/bstats/servers/30910?style=plastic)](https://bstats.org/plugin/bukkit/Szlaban/30910)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/PiotrWey/Szlaban/total?style=plastic&logo=github)


**Szlaban** is a multi-functional Minecraft server security plugin. It consists of multiple modules designed to help you
secure your server against unauthorised activity. Featuring extensive configuration files, you can tweak the plugin to
suit your needs.

## Features

- Advanced firewall system &ndash; protect your server from undesired network traffic with a highly configurable
  firewall. Bundled with a sane default configuration, you can expect a moderate level of protection straight out of the
  box!
- Setup advisories &ndash; helps new owners configure sane defaults for their server, while pointing out any
  suspected configuration issues and offering one-click resolutions for all!
- Highly customisable &ndash; the plugin is written with a modular approach at its core, so you only need to enable the
  modules that you want!

### So, what's with the name?

**Szlaban** (IPA: /ˈʂla.ban/ or SHLA•ban) is a Polish word for a gate or barrier &ndash; like the ones used at car park
entrances. These have the function of preventing unauthorised vehicles from entering a place, and this plugin serves a
similar purpose!

Also, it ends with 'ban', which is pretty funny.


## Installation

_Szlaban_ is a PaperMC plugin, and will only work on Paper and its _downstream_ forks &ndash; Spigot and CraftBukkit
are not supported. This is intentional &ndash; bStats data revealed that about 85% of Bukkit-based servers run
Paper[^1], and since Paper's API provides features not present in the base CraftBukkit API, there was no meaningful
reason to maintain broader compatibility. If you're not already running Paper, you probably should be.

In order to install Szlaban, you will need a Minecraft server running a compatible server software implementation. The
following instructions are given as guidance, and may vary depending on where you host your server.

1. Download the latest version of the plugin from GitHub releases (or your preferred plugin distribution platform)
2. Open your server's file manager
3. Open the `plugins/` directory
4. Upload the plugin jar that you downloaded in step 1
5. Restart your server &ndash; Szlaban should now be loaded!

### Additional Dependencies

_Szlaban_ supports the following optional dependencies, which unlock additional functionality when installed:

- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) &ndash; enables the `protocollib` firewall
  engine as an alternative blocking backend.


## Configuration

> See [configuration.md](docs/configuration.md)


## Developing

_Szlaban_ is well-documented, so getting started with development should be straightforward &ndash; just make sure you
have the requirements, then follow the steps below!

### Requirements

- Java 21+
- Gradle 9.4.1+ (works via wrapper)

### Setup

First, clone the repository and `cd` into the folder.

```shell
git clone https://github.com/PiotrWey/Szlaban.git && cd Szlaban
```

### Building

This project uses Gradle as its build system. It will resolve all dependencies automatically, so you can build the
project using the following command:

```shell
./gradlew build
```

### Testing

Tests are written using JUnit 5, and can be run using Gradle:

```shell
./gradlew test
```

If you would like to manually test functionality on a server, the project includes a Gradle task for this:
```shell
./gradlew runServer
```

### Contributing

> See [CONTRIBUTING.md](CONTRIBUTING.md) for contributor guidelines.

_Szlaban_ has [documentation](docs/README.md)! Make sure to read it if you want to contribute.


## Licence

This project is licensed under the [GNU Lesser General Public License v3.0](LICENCE)
with additional terms under GPL-3.0 section 7 – see [LICENCE.ADDITIONAL_TERMS](LICENCE.ADDITIONAL_TERMS)
for details.


## Footnotes

[^1]: Statistics from bStats; analysed using [PiotrWey/MCSS_DataAnalysis](https://github.com/PiotrWey/MCSS_DataAnalysis/blob/main/bStats.ipynb)