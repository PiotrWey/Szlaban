package dev.piotr_weychan.szlaban.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.piotr_weychan.szlaban.behaviour.Capability;
import dev.piotr_weychan.szlaban.module.AbstractModule;
import dev.piotr_weychan.szlaban.module.ModuleManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;


@SuppressWarnings("UnstableApiUsage")
class CommandManagerTest {
  CommandManager commandManager;
  ModuleManager moduleManager;
  JavaPlugin plugin;

  private static class TestModule extends AbstractModule {
    boolean commandRegistered = false;

    public TestModule(JavaPlugin plugin) {
      super(plugin, EnumSet.noneOf(Capability.class));
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommandNode(@NotNull String id) {
      commandRegistered = true;
      // return a stub
      return Commands.literal(id).build();
    }
  }

  @BeforeEach
  void setUp() {
    plugin = mock(JavaPlugin.class, RETURNS_DEEP_STUBS);
    moduleManager = new ModuleManager(/* plugin */);
    commandManager = new CommandManager(plugin, moduleManager);
  }

  // test if module command registration is called
  @Test
  void registersModuleCommands() {
    AtomicReference<TestModule> ref = new AtomicReference<>();

    // register the supplier, tracing the reference
    moduleManager.registerModule("mod1", () -> {
      TestModule m = new TestModule(plugin);
      ref.set(m);
      return m;
    });

    commandManager.createConfigCommand("test");

    // tracks if command registration was called
    assertTrue(ref.get().commandRegistered);

  }
}
