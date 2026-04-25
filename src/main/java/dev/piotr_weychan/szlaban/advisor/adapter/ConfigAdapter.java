/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.adapter;

import javax.annotation.Nullable;

public interface ConfigAdapter {
  /**
   * Gets the specified value from the adapted config
   * @param path the full path to the entry in the config file. This may change by implementation and is
   *             not standardised
   * @return the corresponding object, or {@code null} if not found
   */
  @Nullable
  Object getValue(String path);

  /**
   * Sets the value at the specified key
   * @param path the full path to the entry in the config file. This may change by implementation and is
   *             not standardised
   * @param value the new value for the key
   */
  void setValue(String path, Object value);

  /**
   * Saves the current version of the adapted config to the corresponding path.
   * @implSpec this is not supposed to throw IOExceptions, make sure they are wrapped in a try-catch with logging
   */
  void save();

}
