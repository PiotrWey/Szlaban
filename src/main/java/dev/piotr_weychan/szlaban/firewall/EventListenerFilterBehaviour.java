/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.ListenerBehaviour;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluationException;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluatorChain;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;

public class EventListenerFilterBehaviour extends ListenerBehaviour {
  private final RuleEvaluatorChain chain;

  public EventListenerFilterBehaviour(BehaviourContext ctx, RuleEvaluatorChain chain) {
    super(ctx);
    this.chain = chain;
  }

  @EventHandler
  public void onConnectionCreate(AsyncPlayerPreLoginEvent event) {
    InetAddress address = event.getAddress();

    RuleType result;

    try {
      result = chain.evaluate(address);
    } catch (RuleEvaluationException e) {
      ctx.plugin().getSLF4JLogger().error("Error while evaluating filter rule: {}", e.getMessage());
      return;
    }

    // Check address and fail
    if (result == RuleType.BLOCK) {
      event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
          Component
              .text("You have been blocked from connecting to this server. Please contact an administrator.\n")
              .color(NamedTextColor.RED)
      );
      ctx.plugin().getSLF4JLogger().info("Disallowed login for {}", address.getHostAddress());
    }
  }

  // use paper's server list ping event
  @EventHandler
  public void onServerListPing(PaperServerListPingEvent event) {
    InetAddress address = event.getAddress();

    RuleType result;

    try {
      result = chain.evaluate(address);
    } catch (RuleEvaluationException e) {
      ctx.plugin().getSLF4JLogger().error("Error while evaluating filter rule: {}", e.getMessage());
      return;
    }

    if (result == RuleType.BLOCK) {
      event.setCancelled(true);
      ctx.plugin().getSLF4JLogger().info("Blocked PaperServerListPingEvent from {}", address.getHostAddress());
    }
  }

//
//  }
}
