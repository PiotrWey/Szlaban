/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.module;

public interface Module {
  void enable();
  void disable();
  boolean isEnabled();
  void onRegister();
  void onUnregister();
}
