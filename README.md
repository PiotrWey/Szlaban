<img src="img/wordmark.png" alt="Szlaban Word mark logo" />

<div align="center">
  <h1>Szlaban</h1>
</div>

**Szlaban** is a multi-functional Minecraft server security plugin. It consists of multiple modules designed to help you
secure your server against unauthorised activity. Featuring extensive configuration files, you can tweak the plugin to
suit your needs.

## Features

- Advanced firewall system &ndash; protect your server from undesired network traffic with a highly configurable
  firewall. Bundled with a sane default configuration, you can expect a moderate level of protection, straight out of
  the box!
- Automatic blocking of suspicious IPs &ndash; automatically flag and block IP addresses from which suspicious activity
  originates.
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

Szlaban is a PaperMC plugin. Needless to say, it will only work on Paper and its _downstream_ forks (so not Spigot or
CraftBukkit). This is intentional &ndash; bStats data revealed that about 85% of Bukkit-based servers run
Paper<sup>1</sup>, and since Paper's API provides some useful features not present in the base CraftBukkit API, there
was no meaningful reason to maintain support. So if you're not using Paper yet, you probably should.

In order to install Szlaban, you will need a Minecraft server running a compatible server software implementation. The
following instructions are given as guidance, and may vary depending on where you host your server.

1. Download the latest version of the plugin from GitHub releases (or your preferred plugin distribution platform)
2. Open your server's file manager
3. Open the `plugins/` directory
4. Upload the plugin jar that you downloaded in step 1
5. Restart your server &ndash; Szlaban should now be loaded!


## Configuration

The plugin is primarily configured using YAML, a simple, human-readable configuration language. If you haven't used YAML
before, you might want to read [Learn X in Y minutes' article on YAML](https://learnxinyminutes.com/yaml/) to understand
the format better.

The main configuration file, `config.yml`, consists of several sections for configuring different aspects of the plugin,
most fields' function and expected values are documented via comments in the file itself, so it should be easy to start
configuring it.

### Managing modules

Near the top of the config file is a `modules:` section, this contains a series of keys corresponding to module names
mapped to booleans. To disable a module, simply set it to `false`, then run `/szlaban reload` in-game to manually reload
the contents of the file.

### Firewall management

The firewall module relies on blocklists and allowlists to cover an extensive range of configurations. Some important
things to note:
- Allowlists take precedence over blocklists
- By default, the module is set to allow-all, to change this to deny-all, you can uncomment some lines in
  [blocklist.txt](src/main/resources/blocklist.txt)
- Blocklist files use custom (but simple) syntax, similar to that of a gitignore file:
  - One entry per line
  - Comments start with `#`
- Each line should have either a CIDR range or a single IP on it. IPs without a specified prefix length will be treated
  as `/32`.
- Blocklists can be loaded from remote sources (over the internet), however, you must make sure the file adheres to this
  format.
- Both IPv4 and IPv6 addresses are supported

Advanced options exist to allow for custom functionality. You should only change these if you know what you're doing,
otherwise, stick to the defaults!

- `engine` Allows you to (attempt to) directly integrated the plugin with your system firewall. Useful if you don't
  want to manage complex rules yourself, though functionality is limited. This is unlikely to work on shared hosting.
- `response` In case you want to audit the plugin or try it out without, this option lets you disable dropping
  connections, and instead logs them. This will only work with `engine: internal`.

#### Advanced Firewall Features

If the basic firewall functionality (i.e. CIDR blocking) isn't enough for you, you can enable the IP lookup system. This
queries an IP info checking service with all IP addresses that make it through the initial rulesets to allow for more
complex blocks, such as ASN or country-based ones.

### Setup Recommendations/Advisories

Szlaban can help you configure your server to make sure you haven't overlooked any recommended settings. Some of these
may not apply to your server, so you can tell the plugin to ignore them. This feature mainly targets beginner server
owners, though some automated setup prompts may prove useful for experienced admins. Nevertheless, if you're an
experienced owner, you may just want to disable this module from loading in the first place.

Some of these changes may change other config files (e.g. `server.properties`)<!--, in testing this didn't seem to cause
any issues, but file IO can sometimes break-->. This functionality can be disabled in the config.






## Planned features


## Footnotes

<sup>1</sup>Statistics from bStats; analysed using [PiotrWey/MCSS_DataAnalysis](https://github.com/PiotrWey/MCSS_DataAnalysis/blob/main/bStats.ipynb)