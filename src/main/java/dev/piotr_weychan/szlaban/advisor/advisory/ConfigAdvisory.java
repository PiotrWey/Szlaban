/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

import dev.piotr_weychan.szlaban.advisor.adapter.ConfigAdapter;
import net.kyori.adventure.text.ComponentLike;

import java.util.Objects;

/**
 * An advisory that checks the value of a key in a configuration file (must be accessed via ConfigAdapter)
 * @param <T> the type of the config entry value
 */
public class ConfigAdvisory<T> extends AbstractAdvisory {
  protected ConfigAdapter config;
  protected String path;
  protected T recommendedValue;

  public ConfigAdvisory(ConfigAdapter config, String path, T recommendedValue, String name, ComponentLike description, ComponentLike reason) {
    super(name, description, reason);

    this.config = config;
    this.path = path;
    this.recommendedValue = recommendedValue;
  }

  @Override
  public boolean isResolved() {
    return Objects.equals(config.getValue(path), recommendedValue);
  }

  @Override
  public void onResolve() {
    config.setValue(path, recommendedValue);
    config.save();
  }
}
