package dev.piotr_weychan.szlaban.firewall.filter;

import java.net.InetAddress;

public interface RuleEvaluator {
  /**
   * Check what action to apply to the provided IP address
   * @param address the IP address to check against the filters
   * @return what action applies to the IP
   */
  RuleType lookup(InetAddress address);
}
