/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

public enum AdvisorySeverity {
  LOW(1),
  MODERATE(2),
  HIGH(3),
  SEVERE(4);

  public final int level;

  AdvisorySeverity(int level) {
    this.level = level;
  }
}
