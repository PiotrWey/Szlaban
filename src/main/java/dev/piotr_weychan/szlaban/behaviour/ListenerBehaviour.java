/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.behaviour;


import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ListenerBehaviour extends AbstractBehaviour implements Listener {

  public ListenerBehaviour(BehaviourContext ctx) {
    super(ctx);
  }

  public void enable() {
    // prevent multi-run
    if (enabled) return;
    enabled = true;
    // register this behaviour on the server
    ctx.plugin().getServer().getPluginManager().registerEvents(this, ctx.plugin());
  }

  public void disable() {
    // prevent multi-run
    if (!enabled) return;
    enabled = false;
    // unregister this behaviour's listener
    HandlerList.unregisterAll(this);
  }
}
