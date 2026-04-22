/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.ListenerBehaviour;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluator;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.List;

public class LoginListenerBehaviour extends ListenerBehaviour {
  private final List<RuleEvaluator> ruleEvaluators;

  public LoginListenerBehaviour(BehaviourContext ctx, List<RuleEvaluator> ruleEvaluators) {
    super(ctx);
    this.ruleEvaluators = ruleEvaluators;
  }

  private boolean checkAddress(InetAddress address) {
    // Check against all the rule evaluators
    for (RuleEvaluator re : ruleEvaluators) {
      if (re.lookup(address) == RuleType.BLOCK) {
        return false;
      }
    }
    return true;
    // Connection OK!
  }

  @EventHandler
  public void onConnectionCreate(AsyncPlayerPreLoginEvent event) {
    InetAddress address = event.getAddress();

    // Check address and fail
    if (!checkAddress(address)) {
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
