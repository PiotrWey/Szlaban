/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.module;

import dev.piotr_weychan.szlaban.behaviour.Behaviour;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule implements Module {
  protected final Plugin plugin;
  protected boolean enabled = false;

  private final List<Behaviour> behaviours = new ArrayList<>();

  public AbstractModule(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Register a behaviour, starting it if the plugin is enabled.
   */
  protected void registerBehaviour(Behaviour behaviour) {
    behaviours.add(behaviour);
    if (enabled) {
      behaviour.start();
    }
  }

  /**
   * Enable the module
   */
  public final void enable() {
    if (enabled) return;

    enabled = true;
    for (Behaviour behaviour : behaviours) {
      try {
        behaviour.start();
      } catch (Exception e) {
        plugin.getSLF4JLogger().error("Failed to start behaviour {}: {}", behaviour.getClass().getSimpleName(), e.getMessage());
      }
    }
  }

  /**
   * Disable the module
   */
  public void disable() {
    if (!enabled) return;

    enabled = false;
    for (Behaviour behaviour : behaviours) {
      try {
        behaviour.stop();
      } catch (Exception e) {
        plugin.getSLF4JLogger().error("Failed to stop behaviour {}: {}", behaviour.getClass().getSimpleName(), e.getMessage());
      }
    }
  }

  /**
   * Queries the module's status
   * @return whether the module is enabled
   */
  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
