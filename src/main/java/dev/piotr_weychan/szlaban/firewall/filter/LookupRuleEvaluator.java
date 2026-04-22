/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.filter;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class LookupRuleEvaluator implements RuleEvaluator {
  private final List<Map<String, String>> rules;
  private final String apiEndpoint;

  public LookupRuleEvaluator(List<Map<String, String>> rules, String apiEndpoint) {
    this.rules = rules;
    this.apiEndpoint = apiEndpoint;
  }

  public RuleType lookup(InetAddress address) {
    // TODO: add logic for looking up the address from the API, then matching rules
    return RuleType.DEFAULT;
  }
}
