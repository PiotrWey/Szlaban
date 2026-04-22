package dev.piotr_weychan.szlaban.firewall.filter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RuleEvaluatorChain implements RuleEvaluator {
  private final Map<InetAddress, RuleType> cache = new ConcurrentHashMap<>();
  private final List<RuleEvaluator> evaluators;

  public RuleEvaluatorChain(List<RuleEvaluator> evaluators) {
    this.evaluators = evaluators;
  }

  public RuleEvaluatorChain() {
    this(new ArrayList<>());
  }

  public void addRuleEvaluator(RuleEvaluator evaluator) {
    evaluators.add(evaluator);
  }

  public RuleType evaluate(InetAddress address) throws RuleEvaluationException {
    // Check cache first
    if  (cache.containsKey(address)) {
      return cache.get(address);
    }

    // evaluate rules in order
    for (RuleEvaluator evaluator : evaluators) {
      RuleType ruleType = evaluator.evaluate(address);
      if (ruleType == RuleType.BLOCK) {
        cache.put(address, ruleType);
        return ruleType;
      };
    }

    cache.put(address, RuleType.DEFAULT);
    return RuleType.DEFAULT;
  }
}
