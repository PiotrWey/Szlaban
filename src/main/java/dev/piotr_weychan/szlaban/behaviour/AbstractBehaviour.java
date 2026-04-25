/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.behaviour;

public abstract non-sealed class AbstractBehaviour implements Behaviour {
  protected final BehaviourContext ctx;
  protected boolean enabled;

  public AbstractBehaviour(BehaviourContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Enable the behaviour
   */
  public abstract void enable();

  /**
   * Disable the behaviour
   */
  public abstract void disable();
}
