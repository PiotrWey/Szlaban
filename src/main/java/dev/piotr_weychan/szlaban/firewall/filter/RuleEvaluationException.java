/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.firewall.filter;

public class RuleEvaluationException extends RuntimeException {
  public RuleEvaluationException(String message) {
    super(message);
  }
  public RuleEvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
  public RuleEvaluationException(Throwable cause) {
    super(cause);
  }
}
