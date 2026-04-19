package dev.piotr_weychan.szlaban.firewall.filter;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class LookupRuleEvaluator implements RuleEvaluator {
  private List<Map<String, String>> rules;
  private String apiEndpoint;

  public LookupRuleEvaluator(List<Map<String, String>> rules, String apiEndpoint) {
    this.rules = rules;
    this.apiEndpoint = apiEndpoint;
  }

  public RuleType lookup(InetAddress address) {
    // TODO: add logic for looking up the address from the API, then matching rules
    return RuleType.DEFAULT;
  }
}
