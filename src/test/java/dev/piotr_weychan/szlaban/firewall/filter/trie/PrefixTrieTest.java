/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.firewall.filter.trie;

import com.google.common.net.InetAddresses;
import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrefixTrieTest {
  @Test
  void simpleMatching() {
    PrefixTrie trie = new PrefixTrie();

    CidrBlock block = CidrBlock.parse("192.168.1.1/32");

    trie.insertRule(block, RuleType.BLOCK);

    assertEquals(RuleType.BLOCK, trie.getRuleType(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.CONTINUE, trie.getRuleType(InetAddresses.forString("192.168.1.2")));
  }

  @Test
  void multipleMatching() {
    PrefixTrie trie = new PrefixTrie();

    // test with CIDR ranges (local IPs)
    trie.insertRule(CidrBlock.parse("192.168.0.0/16"), RuleType.BLOCK);
    trie.insertRule(CidrBlock.parse("172.16.0.0/12"), RuleType.BLOCK);
    trie.insertRule(CidrBlock.parse("10.0.0.0/8"), RuleType.BLOCK);

    // allow 192.168.1.0/24 through
    trie.insertRule(CidrBlock.parse("192.168.1.0/24"), RuleType.ALLOW);

    // test all cases
    assertEquals(RuleType.ALLOW, trie.getRuleType(InetAddresses.forString("192.168.1.1")));
    assertEquals(RuleType.BLOCK, trie.getRuleType(InetAddresses.forString("192.168.127.127")));
    assertEquals(RuleType.CONTINUE, trie.getRuleType(InetAddresses.forString("1.1.1.1")));

  }
}
