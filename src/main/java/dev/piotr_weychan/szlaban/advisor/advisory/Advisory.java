/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

import net.kyori.adventure.text.ComponentLike;


public interface Advisory {
  String getName();
  ComponentLike getDescription();
  ComponentLike getReason();
  AdvisorySeverity getSeverity();

  /**
   * Resolve the advisory
   */
  void resolve();

  /**
   * Check if the advisory applies
   * @return whether the advisory is not implemented
   */
  boolean isResolved();
}
