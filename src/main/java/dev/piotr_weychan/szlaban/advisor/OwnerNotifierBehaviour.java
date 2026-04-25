/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor;

import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.ListenerBehaviour;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

/**
 *
 */
final class OwnerNotifierBehaviour extends ListenerBehaviour {
  private final YamlConfiguration data;
  private final File dataFile;
  private final AdvisorModule advisorModule;

  public OwnerNotifierBehaviour(BehaviourContext ctx, AdvisorModule mod, YamlConfiguration data, File dataFile) {
    super(ctx);
    this.data = data;
    this.dataFile = dataFile;
    this.advisorModule = mod;
  }

  private void showWelcomeMessage(Audience audience) {
    long unresolved = advisorModule.getAdvisories().entrySet().stream()
        .filter(e -> !advisorModule.isResolved(e.getKey()) && !advisorModule.isDismissed(e.getKey()))
        .count();

    if (unresolved == 0) return;

    audience.sendMessage(
        text("━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY)
    );
    audience.sendMessage(
        text("⚠ ", NamedTextColor.GOLD)
            .append(text("Szlaban", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            .append(text(" has found ", NamedTextColor.GRAY))
            .append(text(unresolved + " unresolved advisor" + (unresolved == 1 ? "y" : "ies"), NamedTextColor.RED).decorate(TextDecoration.BOLD))
            .append(text(".", NamedTextColor.GRAY))
    );
    audience.sendMessage(
        text("Run ", NamedTextColor.GRAY)
            .append(text("/szlaban advisor list", NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/szlaban advisor list"))
                .hoverEvent(HoverEvent.showText(text("Click to view advisories"))))
            .append(text(" to view and resolve them.", NamedTextColor.GRAY))
    );
    audience.sendMessage(
        text("━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY)
    );
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent evt) {
    Player player = evt.getPlayer();
    // permission check
    if (!player.hasPermission("szlaban.advisor")) return;

    // check if player has seen this notice
    Set<UUID> uuidStrings = data.getStringList("seen-players").stream()
        .map(UUID::fromString)
        .collect(Collectors.toSet());
    if (uuidStrings.contains(player.getUniqueId())) return;

    // no? show them the interactive notice then!
    ctx.plugin().getServer().getScheduler().scheduleSyncDelayedTask(ctx.plugin(),
        // show welcome message after 5 seconds (5*20 ticks)
        () -> showWelcomeMessage(player), 5 * 20L
    );

    // insert the player's uuid into the set
    uuidStrings.add(player.getUniqueId());
    data.set("seen-players", uuidStrings.stream().map(UUID::toString).toList());

    try {
      data.save(dataFile);
    } catch (IOException e) {
      ctx.plugin().getSLF4JLogger().error("Failed to update advisor data: {}", e.getMessage());
    }

  }
}
