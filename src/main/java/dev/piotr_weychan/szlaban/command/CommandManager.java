/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.piotr_weychan.szlaban.module.Module;
import dev.piotr_weychan.szlaban.module.ModuleManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class CommandManager {
  private final ModuleManager moduleManager;
  private final JavaPlugin plugin;

  public CommandManager(JavaPlugin plugin, ModuleManager moduleManager) {
    this.plugin = plugin;
    this.moduleManager = moduleManager;
  }

  private static final DynamicCommandExceptionType ERROR_INVALID_MODULE = new DynamicCommandExceptionType(module ->
    MessageComponentSerializer.message().serialize(
        Component.text(module + " is not a valid module.")
    )
  );

  private Module findModule(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    String moduleName = StringArgumentType.getString(context, "module");

    @Nullable Module mod = moduleManager.getModule(moduleName);
    if (mod == null) throw ERROR_INVALID_MODULE.create(moduleName);

    return mod;
  }

  public final LiteralCommandNode<CommandSourceStack> createConfigCommand(final String name) {

    LiteralArgumentBuilder<CommandSourceStack> bobTheBuilder = Commands.literal(name) // can we fix it
        .then(module())
        .then(modules())
        .then(reloadConfig());

    for (Map.Entry<String, Module> entry : moduleManager.getModuleEntries()) {
      // try to read the commands
      LiteralCommandNode<CommandSourceStack> moduleCommandNode = entry.getValue().getCommandNode(entry.getKey());
      // skip if the module does not provide a command node
      if (moduleCommandNode == null) continue;
      // register (add the node to the builder)
      bobTheBuilder = bobTheBuilder.then(moduleCommandNode);
    }

    return bobTheBuilder.build(); // yes we can
  }

  private LiteralCommandNode<CommandSourceStack> module() {
    return Commands.literal("module")
        .requires(ctx -> ctx.getSender().hasPermission("szlaban.config"))
        .then(Commands.argument("module", StringArgumentType.string())
            .suggests((ctx, builder) -> {
              for (String module : moduleManager.getModuleIds()) {
                builder.suggest(module);
              }
              return builder.buildFuture();
            })
            .executes(ctx -> {
              Module mod = findModule(ctx);

              ctx.getSource().getSender().sendRichMessage(
                  "<bold><gold>" + mod.getName() + "</bold>\n" +
                      "<yellow>" + mod.getDescription() + "\n" +
                      "Status: " + (mod.isEnabled() ? "<green>enabled" : "<red>disabled")
              );
              return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("enable")
                .executes(ctx -> {
                  Module mod = findModule(ctx);

                  CommandSender sender = ctx.getSource().getSender();

                  if (mod.isEnabled()) {
                    sender.sendRichMessage("<yellow>" + mod.getName() + "<gold> is already enabled!");
                  } else {
                    mod.enable();
                    sender.sendRichMessage("<yellow>" + mod.getName() + "<gold> has been <green>enabled</green>!");
                  }
                  return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("disable")
                .executes(ctx -> {
                  Module mod = findModule(ctx);

                  CommandSender sender = ctx.getSource().getSender();

                  if (!mod.isEnabled()) {
                    sender.sendRichMessage("<yellow>" + mod.getName() + "<gold> is already disabled!");
                  } else {
                    mod.disable();
                    sender.sendRichMessage("<yellow>" + mod.getName() + "<gold> has been <red>disabled</red>!");
                  }
                  return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("reload")
                .executes(ctx -> {
                  // only use as input validation
                  findModule(ctx);
                  String moduleName = StringArgumentType.getString(ctx, "module");

                  CommandSender sender = ctx.getSource().getSender();

                  plugin.reloadConfig();
                  moduleManager.reloadModule(moduleName);

                  sender.sendRichMessage("<gold>Module <yellow>" + moduleName + "</yellow> has been reloaded!");

                  return Command.SINGLE_SUCCESS;
                })
            )
        ).build();
  }

  private LiteralCommandNode<CommandSourceStack> modules() {
    return Commands.literal("modules")
        .requires(ctx -> ctx.getSender().hasPermission("szlaban.config"))
        .executes(ctx -> {
          List<Component> modules = moduleManager.getModuleIds().stream().map(id -> {
            Module module = moduleManager.getModule(id);

            assert module != null;

            return Component.text(module.getName())
                .hoverEvent(HoverEvent.showText(Component.text(module.getDescription())))
                .color(module.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED)
                .asComponent();
          }).toList();

          Component statusMessage = Component.text("The following modules are available:")
              .color(NamedTextColor.GOLD)
              .appendNewline()
              .appendSpace();

          for (int i = 0; i < modules.size(); i++) {
            statusMessage = statusMessage.append(modules.get(i));
            if (i != modules.size() - 1) {
              statusMessage = statusMessage.append(
                  Component.text(", ")
                      .color(NamedTextColor.GRAY)
              );
            }
          }

          ctx.getSource().getSender().sendMessage(statusMessage);
          return Command.SINGLE_SUCCESS;
        }).build();
  }

  private LiteralCommandNode<CommandSourceStack> reloadConfig() {
    return Commands.literal("reload")
        .requires(ctx -> ctx.getSender().hasPermission("szlaban.config"))
        .executes(ctx -> {
          plugin.reloadConfig();
          moduleManager.getModuleIds().forEach(moduleManager::reloadModule);
          ctx.getSource().getSender().sendRichMessage("<gold>Configuration has been reloaded!");
          return Command.SINGLE_SUCCESS;
        }).build();
  }


}
