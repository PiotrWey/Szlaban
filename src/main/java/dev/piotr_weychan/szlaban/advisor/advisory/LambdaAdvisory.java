/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor.advisory;

import net.kyori.adventure.text.ComponentLike;

import java.util.function.BooleanSupplier;

/**
 * A simple advisory that can be queried and resolved using references to functions
 */
public class LambdaAdvisory extends AbstractAdvisory{

  private final BooleanSupplier isApplied;
  private final Runnable apply;

  public LambdaAdvisory(
      BooleanSupplier isApplied, Runnable apply,
      String name, ComponentLike description, ComponentLike reason
  ) {
    super(name,  description, reason);

    this.isApplied = isApplied;
    this.apply = apply;
  }

  @Override
  public boolean isApplied() {
    return isApplied.getAsBoolean();
  }

  @Override
  public void onResolve() {
    apply.run();
  }
}
