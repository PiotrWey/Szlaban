/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.behaviour;

public abstract class AbstractBehaviour implements Behaviour {
  protected final BehaviourContext ctx;
  protected boolean enabled;

  public AbstractBehaviour(BehaviourContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Enable the behaviour
   */
  public abstract void start();

  /**
   * Disable the behaviour
   */
  public abstract void stop();
}
