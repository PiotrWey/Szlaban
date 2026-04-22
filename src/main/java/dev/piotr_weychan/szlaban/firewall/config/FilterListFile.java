/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.config;

import dev.piotr_weychan.szlaban.firewall.model.CidrBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class FilterListFile {

  private final List<CidrBlock> entries = new ArrayList<>();

  public FilterListFile(@NotNull File file) throws IOException {
    // Attempt to create an input stream from the file at the path
    this(new FileInputStream(file));
  }

  public FilterListFile(@NotNull URL remoteUrl) throws IOException {
    this(remoteUrl.openStream());
  }

  // Internal constructor using generic methods that can be applied to both
  // files and URIs
  private FilterListFile(@NotNull InputStream inputStream) throws IOException {

    // try-with resources block, closes
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
    ) {
      for (String line : reader.lines().toList()) {
        // Ignore comments and blank lines
        if (!line.trim().startsWith("#") && !line.trim().isEmpty()) {
          // Remove comments and surrounding whitespace
          String rawBlock = line.split("#", 2)[0].trim();
          // Add the entry, or skip if it's invalid
          try {
            entries.add(CidrBlock.parse(rawBlock));
          } catch (IllegalArgumentException ignored) { /* Ignore invalid CIDRs */ }
        }
      }
    }
  }

  @Contract(pure = true)
  public List<CidrBlock> getEntries() {
    return entries;
  }

}
