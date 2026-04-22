/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.filter.trie;

import dev.piotr_weychan.szlaban.firewall.filter.RuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PrefixTrieNode {
  @Nullable
  public PrefixTrieNode zero = null;

  @Nullable
  public PrefixTrieNode one = null;

  private RuleType ruleType;

  public PrefixTrieNode(@Nullable RuleType ruleType) {
    this.ruleType = Objects.requireNonNullElse(ruleType, RuleType.DEFAULT);
  }

  public PrefixTrieNode() {
    this(null);
  }

  public void setRuleType(RuleType ruleType) {
    this.ruleType = ruleType;
  }

  @NotNull
  public RuleType getRuleType() {
    return ruleType;
  }

}
