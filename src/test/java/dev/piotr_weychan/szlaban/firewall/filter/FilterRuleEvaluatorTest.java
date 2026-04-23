/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.firewall.filter;

import com.google.common.net.InetAddresses;
import dev.piotr_weychan.szlaban.firewall.filter.trie.PrefixTrie;
import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FilterRuleEvaluatorTest {
  @Test
  void blocked() {
    // 192.168.0.0/16
    PrefixTrie trie4 = new PrefixTrie();
    trie4.insertRule(CidrBlock.parse("192.168.0.0/16"), RuleType.BLOCK);

    // fc00::/7
    PrefixTrie trie6 = new PrefixTrie();
    trie6.insertRule(CidrBlock.parse("fc00::/7"), RuleType.BLOCK);

    RuleEvaluator re = new FilterRuleEvaluator(trie4, trie6);

    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("fc00::dead:beef")));
  }

  @Test
  void allowed() {
    // 192.168.0.0/16
    PrefixTrie trie4 = new PrefixTrie();
    trie4.insertRule(CidrBlock.parse("192.168.0.0/16"), RuleType.ALLOW);

    // fc00::/7
    PrefixTrie trie6 = new PrefixTrie();
    trie6.insertRule(CidrBlock.parse("fc00::/7"), RuleType.ALLOW);

    RuleEvaluator re = new FilterRuleEvaluator(trie4, trie6);

    assertEquals(RuleType.ALLOW, re.evaluate(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.ALLOW, re.evaluate(InetAddresses.forString("fc00::dead:beef")));
  }

  @Test
  void notMatched() {
    // 192.168.0.0/16
    PrefixTrie trie4 = new PrefixTrie();
    trie4.insertRule(CidrBlock.parse("192.168.0.0/16"), RuleType.BLOCK);

    // fc00::/7
    PrefixTrie trie6 = new PrefixTrie();
    trie6.insertRule(CidrBlock.parse("fc00::/7"), RuleType.BLOCK);

    RuleEvaluator re = new FilterRuleEvaluator(trie4, trie6);

    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("fc00::dead:beef")));
  }
}
