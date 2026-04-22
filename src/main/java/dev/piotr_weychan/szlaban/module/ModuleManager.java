/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.module;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public final class ModuleManager {
  private final Map<String, Supplier<Module>> moduleSuppliers = new HashMap<>();
  private final Map<String, Module> modules = new HashMap<>();

  private final JavaPlugin plugin;

  public ModuleManager(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Safely register a new module without overwriting previously existing ones.
   * @param id the identifier that should be associated with the module
   * @param supplier a supplier that creates module instances
   */
  public void registerModule(String id, Supplier<Module> supplier) {
    // Register the module supplier in the map, ignoring if already present
    if (moduleSuppliers.putIfAbsent(id, supplier) != null) return;

    // Register a new instance of the module
    createAndRegister(id);
  }

  private void createAndRegister(String id) {
    Supplier<Module> supplier = moduleSuppliers.get(id);
    if (supplier == null) return;

    Module module = supplier.get();

    if (modules.putIfAbsent(id, module) != null) return;

    module.onRegister();
  }

  /**
   * Safely unregister an existing module. Does nothing if the key isn't present.
   * @param id the identifier for the module to remove
   */
  public void unregisterModule(String id) {
    // Attempt to remove module from the map, returning if not present
    Module module = modules.remove(id);
    if (module == null) return;
    // Execute cleanup logic
    // Disable module first
    module.disable();
    // Call unregistration hook
    module.onUnregister();
  }

  /**
   * Reload the module with the specified id
   * @param id the identifier for the module to reload
   */
  public void reloadModule(String id) {
    Supplier<Module> supplier = moduleSuppliers.get(id);
    if (supplier == null) return;

    unregisterModule(id);
    createAndRegister(id);
  }

  /**
   * Gets the module object associated with the identifier, or {@code null} otherwise.
   * @param id the identifier for the module to find
   * @return the module associated with the identifier, or {@code null} if not present
   */
  @Contract(pure = true)
  @Nullable
  public Module getModule(String id) {
    return modules.get(id);
  }

  /**
   * Gets a list of registered module identifiers.
   * @return a list of module identifiers
   */
  @Unmodifiable
  @Contract(pure = true)
  public List<String> getModuleIds() {
    return List.copyOf(modules.keySet());
  }

  /**
   * Gets a list of all registered {@link Module} objects without associated identifiers.
   * <br />
   * There is no way to reverse lookup identifier from an instance of {@link Module}, so this
   * should only be used for bulk operations involving all modules.
   * @return a list of all the registered {@link Module} objects
   */
  @Unmodifiable
  @Contract(pure = true)
  public List<Module> getModules() {
    return List.copyOf(modules.values());
  }


}
