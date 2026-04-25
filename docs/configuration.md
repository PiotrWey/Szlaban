> [Go back](README.md)

# Configuration

This page covers how to customise the behaviour of the plugin by using its various config files. 

## Contents

- [Getting Started](#getting-started)
- [Firewall Management](#firewall-management)
- [Setup Recommendations/Advisories](#setup-recommendationsadvisories-advisor)

## Getting Started

The plugin is primarily configured using YAML, a simple, human-readable configuration language. If you haven't used YAML
before, you might want to read [Learn YAML in Y minutes](https://learnxinyminutes.com/yaml/) to understand the format
better.

The main configuration file, `config.yml`, consists of several sections for configuring different aspects of the plugin,
most fields' function and expected values are documented via comments in the file itself, so it should be easy to start
configuring it.

## Managing modules

Near the top of the config file is a `modules:` section, this contains a series of keys corresponding to module names
mapped to booleans. To disable a module, simply set it to `false`, then run `/szlaban reload` in-game to manually reload
the contents of the file.

## Firewall Management

The firewall module relies on blocklists and allowlists to cover an extensive range of configurations. Some important
things to note:
- Allowlists take precedence over blocklists. More specific rules override less specific ones.
- By default, the module is set to allow-all, to change this to deny-all, you can uncomment some lines in
  [blocklist.cfg](../src/main/resources/blocklist.cfg)
- Blocklist files use custom (but simple) syntax, similar to that of a gitignore file:
  - One entry per line
  - Comments start with `#`
- Each line should have either a CIDR range or a single IP on it. IPs without a specified prefix length will be treated
  as `/32` (or `/128` for IPv6).
- Blocklists can be loaded from remote sources (over the internet), however, you must make sure the file adheres to this
  format.
- Both IPv4 and IPv6 addresses are supported

Advanced options exist to allow for custom functionality. You should only change these if you know what you're doing,
otherwise, stick to the defaults!

- `engine` Allows you to select which blocking engine will be used.
  - `internal` is the default and recommended one, this is the most effective, blocking connections right after they are
     established.
  - `protocollib` uses ProtocolLib to block certain kinds of packets. This is still quite effective, and will prevent
    blocked IPs from connecting, however in some cases they will still be able to see that the server is online.
  - `event` uses Bukkit's event API to block/reject a few events. This will prevent such IPs from connecting to your
    server, but due to limitations in how some events are handled, connecting will return a non-standard result,
    indicating that your server is likely to be online.

### Advanced Firewall Features

If the basic firewall functionality (i.e. CIDR blocking) isn't enough for you, you can enable the IP lookup system. This
queries an IP info checking service with all IP addresses that make it through the initial rulesets to allow for more
complex blocks, such as ASN or country-based ones.

Currently, only blocking rules are supported, and values are simply checked against your provided list. The default
configuration is designed to work with multiple API backends that may have different response formats. See the config
file for full details.

## Setup Recommendations/Advisories (Advisor)

Szlaban can help you configure your server to make sure you haven't overlooked any recommended settings. Some of these
may not apply to your server, so you can tell the plugin to ignore them. This feature mainly targets less experienced
server owners, though some automated setup prompts may prove useful for experienced admins. Nevertheless, if you're an
experienced owner, you may just want to disable this module from loading in the first place.

This module stores all its data in `advisor_data.yml`, i.e. which players have already seen the setup advisories login
message, and which advisories have been manually dismissed. 

If you would like to see the prompt again, simply remove your player UUID from the `seen-players` list 
(https://mcuuid.net/ can help you find your UUID from your username). Dismissed advisories can be checked again by
removing their ID from the `dismissed-advisories` list.
