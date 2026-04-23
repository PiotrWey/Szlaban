/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban;

import dev.piotr_weychan.szlaban.behaviour.Capability;
import dev.piotr_weychan.szlaban.command.ConfigurationCommand;
import dev.piotr_weychan.szlaban.firewall.FirewallModule;
import dev.piotr_weychan.szlaban.module.Module;
import dev.piotr_weychan.szlaban.module.ModuleManager;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;


@SuppressWarnings("unused") // Plugin
public final class Szlaban extends JavaPlugin {
  private final ModuleManager moduleManager;
  @SuppressWarnings("FieldCanBeLocal")
  private final int configVersion = 1;

  private final EnumSet<Capability> capabilities;

  public Szlaban() {
    this.moduleManager = new ModuleManager(this);
    this.capabilities = EnumSet.noneOf(Capability.class);
  }

  private void registerModules() {
    // register all modules here
    moduleManager.registerModule(
        "firewall", () -> new FirewallModule(this, capabilities)
    );
  }

  @SuppressWarnings("UnstableApiUsage")
  private void registerCommands() {
    // register all commands here
    ConfigurationCommand cfgCommand = new ConfigurationCommand(this, moduleManager);

    this.getLifecycleManager().registerEventHandler(
        LifecycleEvents.COMMANDS,
        commands -> commands.registrar().register(cfgCommand.szlabanConfig("szlaban"))
    );
  }

  /**
   * (Re)load modules based on whether they are enabled in the configuration file.
   */
  private void loadModules() {
    ConfigurationSection moduleCfg = getConfig().getConfigurationSection("modules");
    assert moduleCfg != null;

    ArrayList<String> keys = new ArrayList<>(moduleCfg.getKeys(false));

    for (String key : keys) {
      Module module = moduleManager.getModule(key);
      if (module == null) {
        getSLF4JLogger().error("Failed to lode module '{}': Not found", key);
        continue;
      }
      // enable module if config says so
      if (moduleCfg.getBoolean(key)) module.enable();
      // otherwise disable it
      else module.disable();
    }
  }

  private void migrateConfig(int fileConfigVersion) {
    // Migrate config to current version
    // Currently this is just backs up the old config and copies in the new one.
    // More robust migration logic isn't planned at this stage, but is welcome
    if (fileConfigVersion != configVersion) {
      if (fileConfigVersion < configVersion) {
        getSLF4JLogger().info("Old config version found - migrating config file.");
      } else {
        getSLF4JLogger().info("New config version found - migrating config file.");
      }
      // get current config file path and backup path
      Path configPath = getDataFolder().toPath().resolve("config.yml");
      Path backupPath = getDataFolder().toPath().resolve("config.yml.bak");
      // try to backup
      try {
        Files.copy(
            configPath, backupPath, StandardCopyOption.REPLACE_EXISTING
        );
        getSLF4JLogger().info("Old config file backed up");
      } catch (IOException e) {
        // Unable to backup file
        getSLF4JLogger().error("Could not backup old config file. Aborting...");
        getSLF4JLogger().warn(
            "Using old config file version may introduce unexpected errors. "+
                "SUPPORT WILL NOT BE GIVEN!"
        );
        return;
      }
      // save the default config (since it's gone now)
      saveDefaultConfig();

      // FUTURE WORK: add migration logic
    }
  }

  @Override
  public void onLoad() {
    if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) capabilities.add(Capability.PROTOCOL_LIB);
  }

  @Override
  public void onEnable() {
    // Plugin startup logic

    // == Config file loading ==
    saveDefaultConfig();
    // Config file compatibility check & migration
    int fileConfigVersion = getConfig().getInt("config-version");
    migrateConfig(fileConfigVersion); // the migration logic is bad

    // == Module management ==
    registerModules();
    loadModules();

    registerCommands();

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic

  }
}
