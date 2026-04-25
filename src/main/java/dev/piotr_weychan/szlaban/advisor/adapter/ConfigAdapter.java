/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.adapter;

import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;

public abstract class ConfigAdapter {
  protected final JavaPlugin plugin;
  protected final File file;

  public ConfigAdapter(JavaPlugin plugin, File file) {
    this.plugin = plugin;
    this.file = file;
  }

  @Nullable
  public abstract Object getValue(String key);
  public abstract void setValue(String key, Object value);

  public abstract void save();

}
