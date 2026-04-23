/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.module;

import dev.piotr_weychan.szlaban.behaviour.Behaviour;

import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.Capability;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class AbstractModule implements Module {
  protected final Plugin plugin;
  protected boolean enabled = false;
  protected final EnumSet<Capability> capabilities;
  protected final BehaviourContext behaviourContext;

  private final List<Behaviour> behaviours = new ArrayList<>();

  protected AbstractModule(JavaPlugin plugin, EnumSet<Capability> capabilities) {
    // Deduplication syntax
    this.plugin = plugin;
    this.capabilities = capabilities;
    this.behaviourContext = new BehaviourContext(plugin, capabilities);
  }

  /**
   * Get the human-readable name for the module
   * @return the module name
   */
  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  /**
   * Get the module's description
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.getName();
  }

  /**
   * Called when the module is registered, may be used as an alternative to the constructor.
   */
  @Override
  public void onRegister() {}

  /**
   * Called when the module is unregistered.
   */
  @Override
  public void onUnregister() {}

  /**
   * Register a behaviour, starting it if the plugin is enabled.
   */
  protected final void registerBehaviour(Behaviour behaviour) {
    behaviours.add(behaviour);
    if (enabled) {
      behaviour.start();
    }
  }

  /**
   * Enable the module
   */
  @Override
  @MustBeInvokedByOverriders
  public void enable() {
    if (enabled) return;

    enabled = true;
    for (Behaviour behaviour : behaviours) {
      try {
        behaviour.start();
      } catch (Exception e) {
        plugin.getSLF4JLogger().error("Failed to start behaviour {}: {}", behaviour.getClass().getSimpleName(), e.getMessage());
      }
    }
    plugin.getSLF4JLogger().info("Module {} enabled", this.getClass().getSimpleName());
  }

  /**
   * Disable the module
   */
  @Override
  @MustBeInvokedByOverriders
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

    plugin.getSLF4JLogger().info("Module {} disabled", this.getClass().getSimpleName());
  }

  /**
   * Queries the module's status
   * @return whether the module is enabled
   */
  @Override
  @Contract(pure = true)
  public boolean isEnabled() {
    return enabled;
  }
}
