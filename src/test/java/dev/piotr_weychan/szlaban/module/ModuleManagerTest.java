/*
 * Copyright (C) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package dev.piotr_weychan.szlaban.module;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import dev.piotr_weychan.szlaban.behaviour.Capability;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ModuleManagerTest {
  protected static final JavaPlugin mockPlugin = mock(JavaPlugin.class);

  // test module for testing
  private static class TestModule extends AbstractModule {
    public final List<String> functionCalls = new ArrayList<>();

    /**
     * Test-only constructor usage! This sample module does not use plugin or capabilities list,
     * but these should always be available to modules.
     */
    public TestModule() {
      super(mockPlugin, EnumSet.noneOf(Capability.class));
    }

    public void enable() {
      functionCalls.add("enable");
      super.enable();
    }

    public void disable() {
      functionCalls.add("disable");
      super.disable();
    }

    public void onRegister() {
      functionCalls.add("onRegister");
    }

    public void onUnregister() {
      functionCalls.add("onUnregister");
    }
  }

  // test if registration lifecycle methods are called
  @Test
  void registrationLifecycleMethods() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();

    // did it register
    mm.registerModule("mod1", mod1);
    assertEquals(List.of("onRegister"), mod1.functionCalls);

  }

  // test if unregistration lifecycle methods are called
  @Test
  void unregistrationLifecycleMethods() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();

    mm.registerModule("mod1", mod1);
    mm.unregisterModule("mod1");

    assertEquals(List.of("onRegister", "disable", "onUnregister"), mod1.functionCalls);
  }

  // test if we can access modules after registration
  @Test
  void moduleAccess() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();

    mm.registerModule("mod1", mod1);
    assertEquals(mod1, mm.getModule("mod1"));
  }

  // test that duplicate calls to module registration are ignored and do not overwrite
  @Test
  void duplicateRegistration() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();
    TestModule mod2 = new TestModule();

    // test override behaviour
    mm.registerModule("mod1", mod1);
    mm.registerModule("mod1", mod2);

    assertEquals(mod1, mm.getModule("mod1"));
  }

  // test behaviour if an invalid module is accessed
  @Test
  void noModuleAccess() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    assertNull(mm.getModule("mod1"));
  }

  // test behaviour if a removed module is accessed
  @Test
  void removedModuleAccess() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();
    mm.registerModule("mod1", mod1);
    mm.unregisterModule("mod1");

    assertNull(mm.getModule("mod1"));
  }

  // test that registration lifecycle methods are only called once!
  @Test
  void noDuplicateRegistrationCalls() {
    ModuleManager mm = new ModuleManager(mockPlugin);

    TestModule mod1 = new TestModule();

    mm.registerModule("mod1", mod1);
    mm.registerModule("mod1", mod1);

    assertEquals(List.of("onRegister"), mod1.functionCalls);
  }

}
