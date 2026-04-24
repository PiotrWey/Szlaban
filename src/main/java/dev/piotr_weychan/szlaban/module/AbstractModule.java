/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.module;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.piotr_weychan.szlaban.behaviour.Behaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.Capability;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Base class for a Module, provides fundamental implementation details
 * @implSpec All setup and registration should be implemented/called in {@link AbstractModule#onRegister()}, <b>not the
 * constructor!</b> The constructor should only be used to initialise class fields.
 */
public abstract class AbstractModule implements Module {
  protected final JavaPlugin plugin;
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
   * Called when the module is registered.
   * @implSpec All configuration and registration should be run in here, <b>not in the constructor!</b>
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
    plugin.getSLF4JLogger().info("Module {} enabled", this.getName());
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

    plugin.getSLF4JLogger().info("Module {} disabled", this.getName());
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

  @Override
  @Nullable
  @Contract(pure = true)
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> getCommandNode(String name) {
    return null;
  }
}
