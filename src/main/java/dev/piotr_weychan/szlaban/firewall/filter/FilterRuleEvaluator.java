package dev.piotr_weychan.szlaban.firewall.filter;

import dev.piotr_weychan.szlaban.firewall.filter.trie.PrefixTrie;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public class FilterRuleEvaluator implements RuleEvaluator {
  PrefixTrie v4trie;
  PrefixTrie v6trie;

  public FilterRuleEvaluator(PrefixTrie v4trie, PrefixTrie v6trie) {
    this.v4trie = v4trie;
    this.v6trie = v6trie;
  }

  @Nullable
  public RuleType lookup(@NotNull InetAddress address) {
    if (address instanceof Inet4Address addr4) {
      return v4trie.getRuleType(addr4);
    } else if (address instanceof Inet6Address addr6) {
      return v6trie.getRuleType(addr6);
    }
    return null;
  }
}
