/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.behaviour;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class BehaviourContext {
  private final JavaPlugin plugin;
  private final ConfigurationSection config;

  public BehaviourContext(JavaPlugin plugin, ConfigurationSection config) {
    this.plugin = plugin;
    this.config = config;
  }

  public JavaPlugin getPlugin() {
    return plugin;
  }

  public ConfigurationSection getConfig() {
    return config;
  }
}
