/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

import dev.piotr_weychan.szlaban.advisor.adapter.ConfigAdapter;
import dev.piotr_weychan.szlaban.advisor.adapter.ServerPropertiesAdapter;
import io.papermc.paper.configuration.GlobalConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.spigotmc.SpigotConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;


/**
 * Static advisory factory.
 * @implNote New advisories should be added to this class!
 */
public final class Advisories {
  private Advisories() {}

  /**
   * Creates a map of the advisories
   * @param plugin the plugin that has all the configuration methods
   * @return an unmodifiable map of {@link String} -> {@link Advisory}
   */
  @Unmodifiable
  public static Map<String, Advisory> create(JavaPlugin plugin) {
    // register config adapters
    ConfigAdapter serverProps = new ServerPropertiesAdapter(plugin);
    GlobalConfiguration paperConfig = GlobalConfiguration.get();

    Map<String, Advisory> map = new LinkedHashMap<>();

    // register all advisories
    map.put("online_mode",  // chat signing can be exploited
        new LambdaAdvisory(
            // checks whether server uses online mode, or is configured to handle online mode UUIDs through
            // its proxy (when enabled)
            () -> plugin.getServer().getOnlineMode() ||
                (SpigotConfig.bungee && paperConfig.proxies.bungeeCord.onlineMode) ||
                (paperConfig.proxies.velocity.enabled && paperConfig.proxies.velocity.onlineMode),
            // enable and save
            () -> {
              serverProps.setValue("online-mode", true);
              serverProps.save();
            },
            "Disable Chat Signing Enforcement",
            text("Enforces chat signing, preventing players without signing support from joining the server."),
            Component.empty()
                .append(text("The chat signing system introduced in 1.19 allows players to be reported to Mojang"))
                .append(text(" for their chat messages. Such reports can lead to "))
                .append(text("account-level").decorate(TextDecoration.BOLD))
                .append(text(" bans. The system has also been subject to security vulnerabilities in the past. "))
                .append(text("Disabling this enforcement is recommended to protect your players from potentially"))
                .append(text(" having their accounts banned as a result of reports on your server. "))
                .append(text("Disabling chat signing requires an additional plugin."))
        )
    );

    map.put("secure_profile",  // chat signing can be exploited
        new ConfigAdvisory<>(serverProps,
            "enforce-secure-profile", "false",
            "Disable Chat Signing Enforcement",
            text("Allows players without signing support to join the server."),
            Component.empty()
                .append(text("The chat signing system introduced in 1.19 allows players to be reported to Mojang"))
                .append(text(" for their chat messages. Such reports can lead to "))
                .append(text("account-level").decorate(TextDecoration.BOLD))
                .append(text(" bans. The system has also been subject to security vulnerabilities in the past. "))
                .append(text("Disabling this enforcement is recommended to protect your players from potentially"))
                .append(text(" having their accounts banned as a result of reports on your server. "))
                .append(text("Disabling chat signing requires an additional plugin."))
        )
    );

    map.put("rcon",
        new ConfigAdvisory<>(serverProps,
            "enable-rcon", "false",
            "Disable RCON",
            text("Disables remote console access to the server over the network."),
            Component.empty()
                .append(text("RCON allows full console access over the network. If left enabled, even with a "))
                .append(text("password, it presents an unnecessary attack surface and can lead to "))
        )
    );

    map.put("whitelist",
        new LambdaAdvisory(
            () -> plugin.getServer().hasWhitelist(),
            () -> plugin.getServer().setWhitelist(true),
            "Enable Whitelist",
            text("Restricts server access to whitelisted players only."),
            Component.empty()
                .append(text("Without a whitelist, anyone who knows (or finds) your server address can join. "))
                .append(text("A whitelist lets you control exactly who can connect. If you want to open your server "))
                .append(text("up more freely, consider using a plugin that links Minecraft accounts to something "))
                .append(text("like a Discord account, so you still have a layer of control over who gets in."))
        )
    );

    return Map.copyOf(map);
  }
}
