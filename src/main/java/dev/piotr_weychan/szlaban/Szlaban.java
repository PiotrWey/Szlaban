/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.piotr_weychan.szlaban;

import dev.piotr_weychan.szlaban.module.Module;
import dev.piotr_weychan.szlaban.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Szlaban extends JavaPlugin {
  private final ModuleManager moduleManager;

  public Szlaban() {
    this.moduleManager = new ModuleManager(this);
  }

  private void registerModules() {
    // TODO: add a list of all existing modules
//    moduleManager.registerModule(
//        "identifier", new Module()
//    );
  }

  @Override
  public void onEnable() {
    // Plugin startup logic
    registerModules();

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic

  }
}
