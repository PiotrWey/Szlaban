/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

import net.kyori.adventure.text.ComponentLike;

public abstract non-sealed class AbstractAdvisory implements Advisory {
  private final String name;
  private final ComponentLike description;
  private final ComponentLike reason;
  private final AdvisorySeverity severity;

  public AbstractAdvisory(String name, ComponentLike description, ComponentLike reason, AdvisorySeverity severity) {
    this.name = name;
    this.description = description;
    this.reason = reason;
    this.severity = severity;
  }

  public AbstractAdvisory(String name, ComponentLike description, ComponentLike reason) {
    this(name, description, reason, AdvisorySeverity.MODERATE);
  }

  public String getName() {
    return name;
  }

  public ComponentLike getDescription() {
    return description;
  }

  public ComponentLike getReason() {
    return reason;
  }

  public AdvisorySeverity getSeverity() {
    return severity;
  }

  public abstract boolean isResolved();

  /**
   * Called when the advisory is being resolved.
   * @implNote {@link AbstractAdvisory#resolve()} already checks if the advisory is applied, so
   * {@link AbstractAdvisory#isResolved()} is always {@code false} in this function.
   */
  protected abstract void onResolve();

  public final void resolve() {
    if (isResolved()) return;
    onResolve();
  }
}
