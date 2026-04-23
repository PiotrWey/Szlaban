/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall;

import dev.piotr_weychan.szlaban.behaviour.Capability;
import dev.piotr_weychan.szlaban.firewall.config.FilterListFile;
import dev.piotr_weychan.szlaban.firewall.filter.*;
import dev.piotr_weychan.szlaban.firewall.filter.trie.PrefixTrie;
import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;
import dev.piotr_weychan.szlaban.module.AbstractModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

public final class FirewallModule extends AbstractModule {

  public FirewallModule(JavaPlugin plugin, EnumSet<Capability> capabilities) {
    super(plugin, capabilities);
  }

  private FilterListFile resolveFilterList(String loc) throws IOException {
    try {
      // See if we can do a URL
      URI uri = new URI(loc);
      if (uri.getScheme() != null) {
        return new FilterListFile(uri.toURL());
      }
    } catch (URISyntaxException | MalformedURLException ignore) {} // try load file then
    // Instead we do file
    File file = new File(plugin.getDataFolder(), loc);
    return new FilterListFile(file);
  }

  @Override
  @NotNull
  public String getName() {
    return "Firewall";
  }

  @Override
  @NotNull
  public String getDescription() {
    return "An advanced configurable firewall system.";
  }

  /**
   * Read the configuration files and build the filter trie
   */
  private FilterRuleEvaluator buildFilterRule(@NotNull ConfigurationSection firewallConfig) {
    // Read blocklists and allowlists
    List<FilterListFile> allowLists = new ArrayList<>();
    List<FilterListFile> blockLists = new ArrayList<>();

    // read allowlist files
    for (String loc : Objects.requireNonNullElse(
        firewallConfig.getStringList("allowlists"), new ArrayList<String>()
    )) {
      try {
        allowLists.add(resolveFilterList(loc));
      } catch (IOException e) {
        plugin.getSLF4JLogger().error("Failed to read allowlist file '{}': {}", loc, e.getMessage());
      }
    }

    // read blocklist files
    for (String loc : Objects.requireNonNullElse(
        firewallConfig.getStringList("blocklists"), new ArrayList<String>()
    )) {
      try {
        blockLists.add(resolveFilterList(loc));
      } catch (IOException e) {
        plugin.getSLF4JLogger().error("Failed to read blocklist file '{}': {}", loc, e.getMessage());
      }
    }

    // start building the prefix tries with actual rules
    PrefixTrie v4rules = new PrefixTrie();
    PrefixTrie v6rules = new PrefixTrie();

    // unpack the contents of the list files into the trie
    for (FilterListFile blockList : blockLists) {
      for (CidrBlock entry : blockList.getEntries()) {
        if (entry.isIpv4()) v4rules.insertRule(entry, RuleType.BLOCK);
        else if (entry.isIpv6()) v6rules.insertRule(entry, RuleType.BLOCK);
      }
    }

    for (FilterListFile allowList : allowLists) {
      for (CidrBlock entry : allowList.getEntries()) {
        if (entry.isIpv4()) v4rules.insertRule(entry, RuleType.ALLOW);
        else if (entry.isIpv6()) v6rules.insertRule(entry, RuleType.ALLOW);
      }
    }

    return new FilterRuleEvaluator(v4rules, v6rules);

  }

  private LookupRuleEvaluator buildLookupRule(@NotNull ConfigurationSection firewallConfig) {
    ConfigurationSection filterConfig = Objects.requireNonNull(
        firewallConfig.getConfigurationSection("advanced-block-filters")
    );

    String backend = Objects.requireNonNull(
        firewallConfig.getString("_api-backend")
    );

    Map<String, Set<String>> rules = new HashMap<>();

    // Populate filters
    for (String key : filterConfig.getKeys(false)) {
      rules.putIfAbsent(key, new HashSet<>(filterConfig.getStringList(key)));
    }

    return new LookupRuleEvaluator(rules, backend);

  }

  @Override
  public void onRegister() {
    // populate the default configs
    plugin.saveResource("allowlist.cfg", false);
    plugin.saveResource("blocklist.cfg", false);

    // store the config
    ConfigurationSection firewallConfig = Objects.requireNonNull(
      plugin.getConfig().getConfigurationSection("firewall")
    );

    // read settings, setting defaults if not present
    String engine = Objects.requireNonNullElse(firewallConfig.getString("engine"), "protocollib");
    // advanced settings concerning IP lookup
    // rest are loaded in FirewallModule#buildLookupRule
    boolean enableIpLookup = firewallConfig.getBoolean("enableIpLookup");


    // Check if ProtocolLib is present when we need it, otherwise fall back to internal engine
    if (!capabilities.contains(Capability.PROTOCOL_LIB) && engine.equals("protocollib")) {
      plugin.getSLF4JLogger().error("ProtocolLib not available, falling back to internal engine");
      engine = "internal";
    }

    // convert config into a nice set of filters
    RuleEvaluatorChain chain = new RuleEvaluatorChain();

    // build filter lists from our config and add to evaluators

    chain.addRuleEvaluator(buildFilterRule(firewallConfig));

    if (enableIpLookup) {
      try {
        chain.addRuleEvaluator(buildLookupRule(firewallConfig));
      } catch (NullPointerException e) {
        plugin.getSLF4JLogger().error("Error while loading advanced block filters: {}", e.getMessage());
      }
    }

    // register filter behaviours
    if (engine.equals("internal")) {
      registerBehaviour(new LoginListenerBehaviour(behaviourContext, chain));
    } else if (engine.equals("protocollib")) {
      registerBehaviour(new PacketInterceptorBehaviour(behaviourContext, chain));
    }

  }

}
