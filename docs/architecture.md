> [Go back](README.md)

# Architecture

This document covers the high-level architecture of _Szlaban_, explaining how the various systems fit together and why
they are designed the way they are.

## Contents

- [Overview](#overview)
- [Capabilities](#capabilities)
- [Module System](#module-system)
  - [`ModuleManager`](#modulemanager)
  - [`AbstractModule`](#abstractmodule)
- [Behaviour System](#behaviour-system)
  - [`BehaviourContext`](#behaviourcontext)
  - [`AbstractBehaviour`](#abstractbehaviour)
- [Command System](#command-system)

## Overview

_Szlaban_ is structured around a modular architecture, where all functionality is encapsulated in independent units
called _modules_. Each module is responsible for one specific area of functionality, and is designed to be enabled,
disabled, and reloaded independently of the others.

## Capabilities

Before modules are registered, _Szlaban_ detects which optional dependencies are available on the server and stores
them as a set of `Capability` flags. These are passed to every module, allowing them to conditionally enable
functionality that depends on optional plugins (e.g. ProtocolLib).

Currently, the only capability is `PROTOCOL_LIB`, which is detected during `onLoad()`.

## Module System

### `ModuleManager`

The `ModuleManager` is responsible for the lifecycle of all modules. It stores two maps internally &ndash; one mapping
identifiers to module suppliers (factories), and one mapping identifiers to live module instances. This separation is
what allows modules to be reloaded, since the supplier can be used to create a fresh instance without losing the
reference to the factory.

The lifecycle of a module is as follows:

1. **Register** &ndash; `registerModule()` stores the supplier and calls `onRegister()` on the new instance.
2. **Enable** &ndash; `enable()` starts the module and all of its behaviours.
3. **Disable** &ndash; `disable()` stops the module and all of its behaviours.
4. **Unregister** &ndash; `unregisterModule()` disables the module, calls `onUnregister()`, and removes it from the map.
5. **Reload** &ndash; `reloadModule()` unregisters the module and re-registers it using the stored supplier,
   re-enabling it if it was enabled prior to the reload.

### `AbstractModule`

`AbstractModule` provides the core implementation of the module lifecycle. The key design decision here is that all
setup logic should go in `onRegister()`, not the constructor &ndash; the constructor should only initialise fields. This
is because the `ModuleManager` needs the instance to exist before it can call `onRegister()`.

In most cases, you should not need to override `enable()` or `disable()` at all &ndash; that is what behaviours are
for. If you do need to override them, they are marked with `@MustBeInvokedByOverriders`, meaning you must call
`super.enable()` / `super.disable()`, otherwise the behaviour list won't be started or stopped correctly.

Both `Module` and `Behaviour` are sealed interfaces &ndash; `AbstractModule` and `AbstractBehaviour` are the intended
extension points, and all references to modules and behaviours should be through the interfaces rather than the concrete
classes. The interfaces are sealed to ensure that all implementations go through the base classes, which provide the
vital lifecycle logic.

## Behaviour System

### `BehaviourContext`

Rather than passing `plugin` and `capabilities` to every behaviour constructor individually, these are bundled into a
`BehaviourContext` record. This is created once per module and passed to all behaviours registered by that module,
keeping constructor signatures clean and making it easy to add new shared context in the future without changing every
behaviour.

### `AbstractBehaviour`

Behaviours are the smallest unit of functionality in _Szlaban_. Each behaviour should do one specific thing, and
nothing more. They are registered with a module via `registerBehaviour()`, and are automatically enabled and disabled
alongside the module.

The key constraint is that `disable()` must fully reverse everything done in `enable()` &ndash; this is what makes
reliable reloading possible.

## Command System

Commands are handled separately from the behaviour system due to a constraint in Paper's Brigadier API &ndash; commands
must be registered during a specific lifecycle event at startup, and cannot be registered or unregistered at runtime.
This means commands cannot be part of the enable/disable cycle.

To work around this, each module can optionally expose a `LiteralCommandNode` via `getCommandNode()`, which is
collected by the `CommandManager` and registered during the appropriate lifecycle event.