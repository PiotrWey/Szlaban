/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.firewall.filter.config;

import com.google.common.net.InetAddresses;
import dev.piotr_weychan.szlaban.firewall.config.FilterListFile;
import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilterListFileTest {
  @Test
  void simpleRead() throws IOException {
    File simpleFile = new File("src/test/resources/test-list.simple.txt");

    FilterListFile flf = new FilterListFile(simpleFile);

    List<CidrBlock> entries = flf.getEntries();

    List<CidrBlock> expectedEntries = new ArrayList<>();
    expectedEntries.add(new CidrBlock(InetAddresses.forString("192.168.1.1"), 32));

    assertEquals(expectedEntries, entries);
  }

  @Test
  void commentsRead() throws IOException {
    File simpleFile = new File("src/test/resources/test-list.comments.txt");

    FilterListFile flf = new FilterListFile(simpleFile);

    List<CidrBlock> entries = flf.getEntries();

    List<CidrBlock> expectedEntries = new ArrayList<>();
    expectedEntries.add(new CidrBlock(InetAddresses.forString("192.168.0.1"), 32));
    expectedEntries.add(new CidrBlock(InetAddresses.forString("192.168.0.3"), 32));

    assertEquals(expectedEntries, entries);
  }

  @Test
  void ipv6Read() throws IOException {
    File simpleFile = new File("src/test/resources/test-list.ipv6.txt");

    FilterListFile flf = new FilterListFile(simpleFile);

    List<CidrBlock> entries = flf.getEntries();

    List<CidrBlock> expectedEntries = new ArrayList<>();
    expectedEntries.add(new CidrBlock(InetAddresses.forString("fc00::"), 7));

    assertEquals(expectedEntries, entries);
  }

  @Test
  void rangeAddRead() throws IOException {
    File simpleFile = new File("src/test/resources/test-list.range-add.txt");

    FilterListFile flf = new FilterListFile(simpleFile);

    List<CidrBlock> entries = flf.getEntries();

    List<CidrBlock> expectedEntries = new ArrayList<>();
    // v4
    expectedEntries.add(new CidrBlock(InetAddresses.forString("12.34.56.78"), 32));
    expectedEntries.add(new CidrBlock(InetAddresses.forString("12.34.56.79"), 32));
    // v6
    expectedEntries.add(new CidrBlock(InetAddresses.forString("dead:beef::"), 128));
    expectedEntries.add(new CidrBlock(InetAddresses.forString("dead:beef::1"), 128));

    assertEquals(expectedEntries, entries);
  }

  @Test
  void remoteRead() throws IOException, URISyntaxException {
    // try to read the simple file from remote
    URL url = new URI(
        "https://github.com/PiotrWey/Szlaban/raw/refs/heads/main/src/test/resources/test-list.simple.txt"
    ).toURL();

    FilterListFile flf = new FilterListFile(url);

    List<CidrBlock> entries = flf.getEntries();
    List<CidrBlock> expectedEntries = new ArrayList<>();
    expectedEntries.add(new CidrBlock(InetAddresses.forString("192.168.1.1"), 32));

    assertEquals(expectedEntries, entries);
  }
}
