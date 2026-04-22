package dev.piotr_weychan.szlaban.firewall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.piotr_weychan.szlaban.behaviour.AbstractBehaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.firewall.filter.RuleEvaluator;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;

import java.net.InetAddress;
import java.util.*;

public class PacketInterceptorBehaviour extends AbstractBehaviour {

  private PacketAdapter packetAdapter;
  private final List<PacketType> packetTypes;
  private final List<RuleEvaluator> ruleEvaluators;

  public PacketInterceptorBehaviour(BehaviourContext ctx, List<RuleEvaluator> ruleEvaluators) {
    super(ctx);
    this.ruleEvaluators = ruleEvaluators;
    // hardcoded filter packets
    packetTypes = new ArrayList<>();
    // add all packet types we should filter
    packetTypes.add(PacketType.Status.Client.PING);
    packetTypes.add(PacketType.Status.Client.START);
    packetTypes.add(PacketType.Handshake.Client.SET_PROTOCOL);
    packetTypes.add(PacketType.Login.Client.START);
  }

  @Override
  public void start() {
    ctx.plugin().getSLF4JLogger().info("Starting PacketInterceptorBehaviour");

      packetAdapter = new PacketAdapter(ctx.plugin(), ListenerPriority.NORMAL, packetTypes) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
          // get player's IP
          InetAddress address = event.getPlayer().getAddress().getAddress();

          // evaluate rules
          for (RuleEvaluator ruleEvaluator : ruleEvaluators) {
            if (ruleEvaluator.lookup(address) == RuleType.BLOCK) {
              // block the connection
              event.setCancelled(true);
              ctx.plugin().getSLF4JLogger().info("Blocked packet from " + address.getHostAddress());
              return;
            }
          }
          // all good!
        }
      };

    ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);

  }

  @Override
  public void stop() {
    ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
  }
}
