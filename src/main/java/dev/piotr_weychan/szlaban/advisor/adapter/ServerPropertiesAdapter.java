/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.adapter;

import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Properties;

public class ServerPropertiesAdapter implements ConfigAdapter {
  private final Properties properties;
  private final JavaPlugin plugin;
  private final File file;

  public ServerPropertiesAdapter(JavaPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getServer().getPluginsFolder().getParentFile(), "server.properties");

    this.properties = new Properties();

    try (InputStream input = new FileInputStream(file)) {
      this.properties.load(input);
    } catch (FileNotFoundException e) {
      plugin.getSLF4JLogger().error("Unable to locate server.properties at {}", file.getAbsolutePath());
    } catch (IOException e) {
      plugin.getSLF4JLogger().error("Unable to read server.properties at {}", file.getAbsolutePath());
    }
  }

  @Nullable
  @Override
  public Object getValue(String key) {
    return properties.getProperty(key);
  }

  @Override
  public void setValue(String key, Object value) {
    properties.setProperty(key, value.toString());
  }

  @Override
  public void save() {
    try (FileOutputStream stream = new FileOutputStream(file)) {
      properties.store(stream, "Minecraft Server Properties");
    } catch (FileNotFoundException e) {
      plugin.getSLF4JLogger().error("Unable to locate server.properties at {}", file.getAbsolutePath());
    }  catch (IOException e) {
      plugin.getSLF4JLogger().error("Unable to store server.properties at {}", file.getAbsolutePath());
    }
  }
}
