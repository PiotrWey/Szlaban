/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.behaviour;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;

public record BehaviourContext(JavaPlugin plugin, EnumSet<Capability> capabilities) {}
