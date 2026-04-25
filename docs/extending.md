> [Go back](README.md)

# Extending

This section covers how to extend the plugin with your own module(s)!

## Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [Registering Behaviours](#registering-behaviours)
- [Registering Commands](#registering-commands)
- [Registering Your Module](#registering-your-module)
- [Testing](#testing)
- [Afterword](#afterword)

## Introduction

Everything _Szlaban_ does is achieved through modules &ndash; independent units of code that are designed to achieve one
specific goal. If you look at [Szlaban.java](../src/main/java/dev/piotr_weychan/szlaban/Szlaban.java), you'll be able to
see that everything is handled through them. Therefore, before you start, you should have a clear idea of what your
module will do.

> [!IMPORTANT]
> This tutorial assumes you have followed the instructions in [Developing](../README.md#developing).

## Getting Started

First of all, you'll want to come up with a name for your module; in this document we'll be using `example`, though your
name should be more descriptive (e.g. `firewall` for a firewall module).

Once you are ready to start implementing your module, create an `example/ExampleModule.java` file with the following
contents:

```java
// example/ExampleModule.java
package dev.piotr_weychan.szlaban.example;

import dev.piotr_weychan.szlaban.behaviour.Capability;
import dev.piotr_weychan.szlaban.module.AbstractModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;

/**
 * An example module to demonstrate how to implement modules!
 */
public class ExampleModule extends AbstractModule {

  public ExampleModule(JavaPlugin plugin, EnumSet<Capability> capabilities) {
    super(plugin, capabilities);
  }

  @Override
  public void onRegister() {
    // Registration logic goes here
    plugin.getSLF4JLogger().info("Hello from ExampleModule!");
  }

}
```

Look at [`AbstractModule`](../src/main/java/dev/piotr_weychan/szlaban/module/AbstractModule.java) for a list of methods
that you might want to override.


## Registering Behaviours

Each module has its own set of _behaviours_, which are more finely-grained units of behaviour &ndash; these should be
designed to do the smallest task that they can. This behaviour is useful as it means that you can disable (or
conditionally load behaviours based on your config, if the need should arise).

To add your first behaviour, create the file for it (`example/ExampleBehaviour.java`):

```java
// example/ExampleBehaviour.java
package dev.piotr_weychan.szlaban.example;

import dev.piotr_weychan.szlaban.behaviour.AbstractBehaviour;
import dev.piotr_weychan.szlaban.behaviour.BehaviourContext;

public class ExampleBehaviour extends AbstractBehaviour {

  // If your behaviour needs to access more things than are provided by the context, pass them here.
  public ExampleBehaviour(BehaviourContext ctx) {
    super(ctx);
  }

  @Override
  public void enable() {
    // Logic for enabling the behaviour, e.g. registering it with the server, or hooking whatever it affects
    ctx.plugin().getSLF4JLogger.info("Hello from ExampleBehaviour!");
  }

  @Override
  public void disable() {
    // Cleanup logic, this must reverse anything done in enable()
    ctx.plugin().getSLF4JLogger.info("Goodbye from ExampleBehaviour!");
  }
}
```

Then add it to your module's behaviour list in the `onRegister()` method using `registerBehaviour(Behaviour)`, passing
the module's behaviourContext as well as any additional parameters to the behaviour:

```java
// example/ExampleModule.java

  @Override
  public void onRegister() {
    // Example log message
    plugin.getSLF4JLogger().info("Hello from ExampleModule!");
    
    // Registration logic goes here
    registerBehaviour(new ExampleBehaviour(behaviourContext));
  }

  //...
```

All behaviours registered in this way will be automatically enabled when `ExampleModule#enable()` is called, and disabled
with `ExampleModule#disable()`. Unless you need custom module-level logic, you probably shouldn't be overriding these
anyway.

## Registering Commands

Behaviours are a versatile tool for adding functionality to your module, but they can't accomplish everything. Custom
commands are one such example. _Szlaban_ uses Paper's wrapper of Mojang's brigadier command system, which requires
commands to be registered during a specific stage during startup. This clashes with the Module and Behaviour systems, as
these are designed to be enabled, disabled, and reloaded during runtime.

If you do not need commands, just don't override the method.

The solution? `Module#getCommandNode(String)` &ndash; a method on each module that allows registering custom commands
with your module.

Simply override the method and make it return a `LiteralCommandNode<CommandSourceStack>` &ndash; see the
[PaperMC docs](https://docs.papermc.io/paper/dev/command-api/basics/introduction/) for information on how to build these
commands in the first place.

An example command might look like this:

```java
// example/ExampleModule.java

// Add these imports:
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

  // ...

  // Then override the method:
  @Override
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> getCommandNode(@NotNull String name) {

    return Commands.literal(name)
        .requires(ctx -> ctx.getSender().hasPermission("szlaban.%s".formatted(name)))
        .executes(ctx -> {
          // Greet the player
          ctx.getSource().getSender().sendRichMessage("<green>Hello from ExampleModule!");
          return Command.SINGLE_SUCCESS;
        })
        .build();
  }

```

> [!NOTE]
> The `@SuppressWarnings("UnstableApiUsage")` is there as Brigadier commands seem to be marked as unstable in 1.21.1.
> This has not caused problems in testing, so it should be fine to keep.


## Registering Your Module

Once you're ready to add your module to the base plugin, simply register it in `Szlaban#registerModules()`, as shown
below:

```java
// Szlaban.java

  private void registerModules() {
    // register all modules here
    moduleManager.registerModule(
        "firewall", () -> new FirewallModule(this, capabilities)
    );
    moduleManager.registerModule(
        "advisor", () -> new AdvisorModule(this, capabilities)
    );
    // ADD YOUR MODULE HERE:
    moduleManager.registerModule(
      "example", () -> new ExampleModule(this, capabilities)  
    );
  }
  
  // ...

```

`ModuleManager` takes a supplier for your module, so make sure to keep the lambda function syntax.


## Testing

### Unit Tests

The base classes for `Module`s and `Behaviour`s already have unit tests written to verify that they work correctly, so
you don't need to re-test functionality that is already covered by them. If your module contains logic that is complex
enough to warrant unit tests, it should be designed to be loosely coupled from the module system so that it can be
tested independently.

### Integration Testing

_Szlaban_ is a Minecraft plugin. Like all plugins or mods, it is designed to modify how the game works, so the best way
to verify that your module works correctly is by playing the game. By now you should have a clear idea of what your
module does, what differentiates correct behaviour from faulty behaviour, and what the potential points of failure are.

Tests for modules will rarely look the same, since each module should be doing something different. To help you come up
with a testing strategy, try to answer the following questions:

1. What does your module do?
2. What does correct behaviour look like?
3. What does incorrect behaviour look like?
4. What specific situations does your module apply in?

Once you have answers, aim to recreate those situations in a testing environment. As an example, the firewall module is
designed to block packets from specific IP ranges and based on API response data &ndash; a good test for this will
involve connecting from different IPs and attempting to trigger as many rules as possible.

> [!TIP]
> Some modules may require multiple players to test properly. In these cases, it's worth recruiting a friend or two to
> help you out.


## Afterword

This tutorial only covers the basics &ndash; for anything more advanced, the source code is your best friend. The
codebase is documented with Javadoc, so it's worth having a read through before diving in. Good luck!