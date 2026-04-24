/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.piotr_weychan.szlaban.behaviour.AbstractBehaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluationException;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluatorChain;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

public class ProtocolLibFilterBehaviour extends AbstractBehaviour {

  private PacketAdapter packetAdapter;
  private final List<PacketType> packetTypes;
  private final RuleEvaluatorChain chain;

  public ProtocolLibFilterBehaviour(BehaviourContext ctx, RuleEvaluatorChain chain) {
    super(ctx);
    this.chain = chain;
    // hardcoded filter packets
    packetTypes = new ArrayList<>();
    // add all packet types we should filter
    packetTypes.add(PacketType.Status.Client.PING);
    packetTypes.add(PacketType.Status.Client.START);
    packetTypes.add(PacketType.Status.Server.SERVER_INFO);
    packetTypes.add(PacketType.Configuration.Client.CLIENT_INFORMATION);
    packetTypes.add(PacketType.Handshake.Client.SET_PROTOCOL);
    packetTypes.add(PacketType.Login.Client.START);
  }

  @Override
  public void start() {
      packetAdapter = new PacketAdapter(ctx.plugin(), ListenerPriority.HIGH, packetTypes) {
        private void handleEvent(PacketEvent event, String direction) {
          // get player's IP (null-safe)
          InetSocketAddress sockAddr = event.getPlayer().getAddress();
          if (sockAddr == null) return;
          InetAddress address = sockAddr.getAddress();

          RuleType result;

          try {
            result = chain.evaluate(address);
          } catch (RuleEvaluationException e) {
            ctx.plugin().getSLF4JLogger().error("Error while evaluating filter rule: {}", e.getMessage());
            return;
          }

          ctx.plugin().getSLF4JLogger().debug("Caught {} packet from {}", direction, address.getHostAddress());

          // Check address and fail
          if (result == RuleType.BLOCK) {
            // block the connection
            event.setCancelled(true);
            ctx.plugin().getSLF4JLogger().info("Blocked {} packet from {}", direction, address.getHostAddress());
          }
          // all good!
        }

        @Override
        public void onPacketReceiving(PacketEvent event) { handleEvent(event, "inbound"); }

        @Override
        public void onPacketSending(PacketEvent event) { handleEvent(event, "outbound"); }
      };

    ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);

  }

  @Override
  public void stop() {
    ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
  }
}
