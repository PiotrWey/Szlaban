/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.module;

public interface Module {
  void enable();
  void disable();
  boolean isEnabled();
  void onRegister();
  void onUnregister();
}
