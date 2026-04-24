/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.module;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import javax.annotation.Nullable;

public interface Module {
  String getName();
  String getDescription();
  void enable();
  void disable();
  boolean isEnabled();
  void onRegister();
  void onUnregister();

  @SuppressWarnings("UnstableApiUsage")
  @Nullable LiteralCommandNode<CommandSourceStack> getCommandNode(String name);
}
