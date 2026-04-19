package dev.piotr_weychan.szlaban.firewall.filter.trie;

import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;
import org.jetbrains.annotations.Contract;

import java.net.InetAddress;
import java.util.Objects;

public final class PrefixTrie extends PrefixTrieNode{
  public PrefixTrie() {
    super();
  }

  @Contract(pure = true)
  public RuleType getRuleType(InetAddress address) {
    byte[] addressBytes = address.getAddress();

    PrefixTrieNode node = this;
    RuleType ruleType = RuleType.DEFAULT;

    for (byte b : addressBytes) {
      for (int i = 7; i >= 0; --i) {
        // calculate the selected bit
        int bit = (((b & 0xff) >> i) & 1);
        // pick which leaf to traverse next
        PrefixTrieNode next = (bit == 0) ? node.zero : node.one;
        // if it's null, return our most recent node
        if (Objects.isNull(next)) return ruleType;
        node = next;
        // check the rule type
        // if not default, update the latest found rule
        RuleType rule = node.getRuleType();
        if (rule != RuleType.DEFAULT) {
          ruleType = rule;
        }
      }
    }

    return ruleType;
  }

  public void insertRule(CidrBlock block, RuleType ruleType) {
    byte[] addressBytes = block.address().getAddress();

    PrefixTrieNode node = this;
    int depth = 0;
    for (byte b : addressBytes) {
      for (int i = 7; i >= 0; --i) {
        // insert and return if we're at the prefix length's depth
        if (depth == block.prefixLength()) {
          node.setRuleType(ruleType);
          return;
        };
        // get the next bit
        int bit = ((b & 0xff) >> i) & 1;

        // insert the next node, or traverse if exists
        if (bit == 0) {
          if (node.zero == null) node.zero = new PrefixTrieNode();
          node = node.zero;
        } else {
          if (node.one == null) node.one = new PrefixTrieNode();
          node = node.one;
        }
        // increase level
        depth++;
      }
    }

    // if we traversed the full tree and haven't returned
    // insert the rule here
    node.setRuleType(ruleType);
  }

}
