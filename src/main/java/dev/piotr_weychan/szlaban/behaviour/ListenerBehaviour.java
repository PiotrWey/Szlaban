/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.behaviour;


import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ListenerBehaviour extends AbstractBehaviour implements Listener {

  public ListenerBehaviour(BehaviourContext ctx) {
    super(ctx);
  }

  public void start() {
    // prevent multi-run
    if (enabled) return;
    enabled = true;
    // register this behaviour on the server
    ctx.getPlugin().getServer().getPluginManager().registerEvents(this, ctx.getPlugin());
  }

  public void stop() {
    // prevent multi-run
    if (!enabled) return;
    enabled = false;
    // unregister this behaviour's listener
    HandlerList.unregisterAll(this);
  }
}
