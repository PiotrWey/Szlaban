/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.model;

import com.google.common.net.InetAddresses;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public record CidrBlock(InetAddress address, int prefixLength) {
  /**
   * Parse a string representation of a CIDR block into a {@link CidrBlock} object. Supports IPv4 and IPv6.
   * @param input a string representation of a CIDR block (e.g. {@code 10.0.0.0/8}) or an individual IP address.
   * @return a {@link CidrBlock} representation of the address
   */
  public static CidrBlock parse(String input) {
    // explode
    String[] exploded = input.split("/", 2);
    // get IP portion
    InetAddress address = InetAddresses.forString(exploded[0]);

    // if prefix length is not provided, assume it's an individual IP, in which
    // case it depends on address type
    int prefixLength = exploded.length > 1 ? Integer.parseInt(exploded[1]) : (
        address instanceof Inet6Address ? 128 : 32
    );

    return new CidrBlock(address, prefixLength);
  }

  public boolean isIpv4() {
    return address instanceof Inet4Address;
  }

  public boolean isIpv6() {
    return address instanceof Inet6Address;
  }
}
