/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

import dev.piotr_weychan.szlaban.behaviour.AbstractBehaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluationException;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluatorChain;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import io.netty.channel.*;
import io.papermc.paper.network.ChannelInitializeListener;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.InetSocketAddress;

//import static io.papermc.paper.network.ChannelInitializeListenerHolder.*;

/**
 * Experimental filter behaviour using NMS (net.minecraft.server), or server internals.
 * This MAY CAUSE INSTABILITY and uses pretty hacky workarounds for accessing NMS (reflection!).
 */

class InternalFilterBehaviour extends AbstractBehaviour {
  private RuleEvaluatorChain chain;

  public InternalFilterBehaviour(BehaviourContext ctx, RuleEvaluatorChain chain) throws IllegalStateException {
    super(ctx);
    this.chain = chain;

    // Check if this module is usable in the first place
    // Prevents runtime errors occurring if the private API changes
    try {
      Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Failed to locate internal PaperMC networking library", e);
    }
  }

  @Override
  public void start() {
    // ctx gets overridden in the listener adding context
    BehaviourContext behaviourContext = ctx;

    ChannelInitializeListenerHolder.addListener(Key.key("szlaban", "firewall"), // szlaban:firewall
        // add filter
        channel -> channel.pipeline().addFirst("szlaban_firewall",
            // create filter
            new ChannelInboundHandlerAdapter() {
              @Override
              public void channelActive(ChannelHandlerContext ctx) throws Exception {
                // Extract the address from remote
                // The returned object *should* be of type InetSocketAddress, so the cast should just work
                InetAddress address = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress();

                RuleType result = null;

                try {
                  result = chain.evaluate(address);
                } catch (RuleEvaluationException e) {
                  behaviourContext.plugin().getSLF4JLogger().error("Error while evaluating filter rule: {}", e.getMessage());
                  return;
                }

                if (result == RuleType.BLOCK) {
                  ctx.disconnect();
                  behaviourContext.plugin().getSLF4JLogger().info("Blocked packet from {}", address.getHostAddress());
                }
                // Let the connection continue, since it's not blocked
              }
            }
          )
    );
  }

  @Override
  public void stop() {
    ChannelInitializeListenerHolder.removeListener(Key.key("szlaban", "firewall")); // szlaban:firewall
  }
}
