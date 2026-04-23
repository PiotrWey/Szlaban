package dev.piotr_weychan.szlaban.firewall.filter;

import com.google.common.net.InetAddresses;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LookupRuleEvaluatorTest {
  // TODO: remove dependency on live web requests
  // requires patching LookupRuleEvaluator to allow passing a HttpClient though...

  @Test
  void rootConfigBlock() {
    // 1.1.1.1 is registered as Australia (AU)
    Map<String, Set<String>> rules = Collections.singletonMap("country_code", Set.of("AU"));
    String endpoint = "https://ipwho.is/%s";

    LookupRuleEvaluator re = new LookupRuleEvaluator(rules, endpoint);

    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("1.1.1.1")));
    assertNotEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("8.8.8.8")));
  }

  // test whether nested rules are evaluated properly (and result in a block)
  @Test
  void nestedConfigBlock() {
    // ASN for 1.1.1.1 should be 13335
    Map<String, Set<String>> rules = Collections.singletonMap("connection.asn", Set.of("13335"));
    String endpoint = "https://ipwho.is/%s";

    LookupRuleEvaluator re = new LookupRuleEvaluator(rules, endpoint);

    assertEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("1.1.1.1")));
    assertNotEquals(RuleType.BLOCK, re.evaluate(InetAddresses.forString("8.8.8.8")));
  }

  // test that when no rule matches then the IP is allowed through
  @Test
  void noBlock() {
    Map<String, Set<String>> rules = Collections.emptyMap();
    String endpoint = "https://ipwho.is/%s";

    LookupRuleEvaluator re = new LookupRuleEvaluator(rules, endpoint);

    assertEquals(RuleType.CONTINUE, re.evaluate(InetAddresses.forString("1.1.1.1")));
    assertEquals(RuleType.CONTINUE, re.evaluate(InetAddresses.forString("8.8.8.8")));
  }
}
