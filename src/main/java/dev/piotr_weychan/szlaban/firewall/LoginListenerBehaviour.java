/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

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

public class LoginListenerBehaviour extends ListenerBehaviour {
  private final RuleEvaluatorChain chain;

  public LoginListenerBehaviour(BehaviourContext ctx, RuleEvaluatorChain chain) {
    super(ctx);
    this.chain = chain;
  }

  @EventHandler
  public void onConnectionCreate(AsyncPlayerPreLoginEvent event) {
    InetAddress address = event.getAddress();

    RuleType result = null;

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
    }
  }

  // ServerListPingEvents are not cancellable
//  @EventHandler
//  public void onServerListPing(ServerListPingEvent event) {
//    InetAddress address = event.getAddress();
//
//    event.setMaxPlayers(0);
//    event.motd(Component.text(""));
//    event.
//
//
//  }
}
