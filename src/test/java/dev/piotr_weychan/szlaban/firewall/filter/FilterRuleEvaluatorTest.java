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

public class FilterRuleEvaluatorTest {
  @Test
  void ruleEvaluation() {
    PrefixTrie trie4 = new PrefixTrie();
    trie4.insertRule(new CidrBlock(InetAddresses.forString("192.168.0.0"), 16), RuleType.BLOCK);

    PrefixTrie trie6 = new PrefixTrie();
    trie6.insertRule(new CidrBlock(InetAddresses.forString("fc00::"), 7), RuleType.BLOCK);

    RuleEvaluator re = new FilterRuleEvaluator(trie4, trie6);

    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("fc00::dead:beef")));
  }
}
