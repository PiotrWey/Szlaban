/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.module;

import dev.piotr_weychan.szlaban.behaviour.AbstractBehaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;
import dev.piotr_weychan.szlaban.behaviour.Capability;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractModuleTest {
  private static final JavaPlugin mockPlugin = mock(JavaPlugin.class);

  private static class TestBehaviour extends AbstractBehaviour {
    public int started = 0;
    public int stopped = 0;

    public TestBehaviour() {
      super(mock(BehaviourContext.class));
    }

    @Override
    public void enable() {
      started++;
    }

    @Override
    public void disable() {
      stopped++;
    }
  }

  private static class TestModule extends AbstractModule {
    protected TestModule() {
      super(mockPlugin, EnumSet.noneOf(Capability.class));
    }
  }

  @BeforeEach
  void setup() {
    Logger logger = mock(Logger.class);
    when(mockPlugin.getSLF4JLogger()).thenReturn(logger);
  }

  // test that behaviours are correctly stopped and started when enable/disable is called
  @Test
  void behaviourEnabling() {
    TestModule mod = new TestModule();
    TestBehaviour behaviour = new TestBehaviour();

    mod.registerBehaviour(behaviour);

    // simulate registration
    mod.onRegister();

    assertEquals(0, behaviour.started);

    mod.enable();
    assertEquals(1, behaviour.started);
  }

  @Test
  void behaviourDisabling() {
    TestModule mod = new TestModule();
    TestBehaviour behaviour = new TestBehaviour();

    mod.registerBehaviour(behaviour);

    // simulate registration
    mod.onRegister();

    assertEquals(0, behaviour.stopped);

    mod.enable();
    assertEquals(0, behaviour.stopped);

    mod.disable();
    assertEquals(1, behaviour.stopped);

  }

  // test that behaviours are automatically enabled when registered into an enabled module
  @Test
  void behaviourEnablingWhenActive() {
    TestModule mod = new TestModule();
    TestBehaviour behaviour = new TestBehaviour();

    mod.onRegister();

    mod.enable();
    assertEquals(0, behaviour.started);

    mod.registerBehaviour(behaviour);
    assertEquals(1, behaviour.started);
  }

  @Test
  void enabledAfterEnable() {
    TestModule mod = new TestModule();

    mod.onRegister();

    mod.enable();

    assertTrue(mod.isEnabled());
  }

  @Test
  void disabledByDefault() {
    TestModule mod = new TestModule();

    mod.onRegister();

    assertFalse(mod.isEnabled());
  }

  @Test
  void disabledAfterDisable() {
    TestModule mod = new TestModule();

    mod.onRegister();

    mod.enable();
    mod.disable();

    assertFalse(mod.isEnabled());
  }

  @Test
  void duplicateEnable() {
    TestModule mod = new TestModule();
    mod.onRegister();

    mod.enable();
    mod.enable();
    assertTrue(mod.isEnabled());
  }

  @Test
  void duplicateDisable() {
    TestModule mod = new TestModule();
    mod.onRegister();

    mod.enable();

    mod.disable();
    mod.disable();

    assertFalse(mod.isEnabled());
  }

  @Test
  void duplicateEnableBehaviours() {
    TestModule mod = new TestModule();
    mod.onRegister();

    TestBehaviour bhv1 = new TestBehaviour();

    mod.registerBehaviour(bhv1);

    mod.enable();
    assertEquals(1, bhv1.started);
    mod.enable();
    assertEquals(1, bhv1.started);

  }

  @Test
  void duplicateDisableBehaviours() {
    TestModule mod = new TestModule();
    mod.onRegister();

    TestBehaviour bhv1 = new TestBehaviour();

    mod.registerBehaviour(bhv1);

    mod.enable();

    mod.disable();
    assertEquals(1, bhv1.stopped);
    mod.disable();
    assertEquals(1, bhv1.stopped);

  }

  @Test
  void enableMultiple() {
    TestModule mod = new TestModule();
    mod.onRegister();

    TestBehaviour bhv1 = new TestBehaviour();
    TestBehaviour bhv2 = new TestBehaviour();
    TestBehaviour bhv3 = new TestBehaviour();

    mod.registerBehaviour(bhv1);
    mod.registerBehaviour(bhv2);
    mod.registerBehaviour(bhv3);

    mod.enable();

    assertEquals(1, bhv1.started);
    assertEquals(1, bhv2.started);
    assertEquals(1, bhv3.started);
  }

  @Test void disableMultiple() {
    TestModule mod = new TestModule();
    mod.onRegister();

    TestBehaviour bhv1 = new TestBehaviour();
    TestBehaviour bhv2 = new TestBehaviour();
    TestBehaviour bhv3 = new TestBehaviour();

    mod.registerBehaviour(bhv1);
    mod.registerBehaviour(bhv2);
    mod.registerBehaviour(bhv3);

    mod.enable();
    mod.disable();

    assertEquals(1, bhv1.stopped);
    assertEquals(1, bhv2.stopped);
    assertEquals(1, bhv3.stopped);
  }
}
