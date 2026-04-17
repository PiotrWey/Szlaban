/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.piotr_weychan.szlaban;

import dev.piotr_weychan.szlaban.module.Module;
import dev.piotr_weychan.szlaban.module.ModuleManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


public final class Szlaban extends JavaPlugin {
  private final ModuleManager moduleManager;
  private final int configVersion = 1;

  public Szlaban() {
    this.moduleManager = new ModuleManager(this);
  }

  private void registerModules() {
    // TODO: add a list of all existing modules
//    moduleManager.registerModule(
//        "identifier", new Module()
//    );
  }

  /**
   * (Re)load modules based on whether they are enabled in the configuration file.
   */
  private void loadModules() {
    ConfigurationSection moduleCfg = getConfig().getConfigurationSection("modules");
    assert moduleCfg != null;

    ArrayList<String> keys = new ArrayList<>(moduleCfg.getKeys(false));

    for (String key : keys) {
      // enable module if config says so
      if (moduleCfg.getBoolean(key)) moduleManager.getModule(key).enable();
      // otherwise disable it
      else moduleManager.getModule(key).disable();
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



  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic

  }
}
