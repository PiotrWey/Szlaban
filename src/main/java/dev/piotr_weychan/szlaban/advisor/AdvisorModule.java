/*
 * Copyright (c) 2026 Piotr Weychan
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * Additional terms under GPL-3.0 section 7 apply – see LICENCE.ADDITIONAL_TERMS.
 */

package dev.piotr_weychan.szlaban.advisor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.piotr_weychan.szlaban.advisor.advisory.Advisories;
import dev.piotr_weychan.szlaban.advisor.advisory.Advisory;
import dev.piotr_weychan.szlaban.behaviour.Capability;
import dev.piotr_weychan.szlaban.module.AbstractModule;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 *
 */
public final class AdvisorModule extends AbstractModule {
  private final Map<String, Advisory> advisories = new HashMap<>();
  private final Set<String> dismissedAdvisories = new HashSet<>();

  private final File advisorDataFile;
  private final YamlConfiguration advisorData;

  public AdvisorModule(JavaPlugin plugin, EnumSet<Capability> capabilities) {
    super(plugin, capabilities);
    this.advisorDataFile = new File(plugin.getDataFolder(), "advisor_data.yml");
    this.advisorData = YamlConfiguration.loadConfiguration(this.advisorDataFile);
  }

  @Override
  @NotNull
  public String getName() {
    return "Advisor";
  }

  @Override
  @NotNull
  public String getDescription() {
    return "Flags common misconfigurations and offers setup advice";
  }

  /**
   * Register a single advisory
   * @param id the id to associate with the advisory
   * @param advisory the advisory itself
   */
  private void registerAdvisory(String id, Advisory advisory) {
    advisories.putIfAbsent(id, advisory);
  }

  /**
   * Dismisses an advisory
   */
  private void dismissAdvisory(String id) {
    dismissedAdvisories.add(id);

    // convert to list
    advisorData.set("dismissed", new ArrayList<>(dismissedAdvisories));

    // save to file
    try {
      advisorData.save(advisorDataFile);
    } catch (IOException e) {
      plugin.getSLF4JLogger().error("Could not save advisory data to {}", advisorDataFile.getAbsolutePath());
    }
  }

  /**
   * Register all advisories.
   * @implNote Advisories are stored in {@link Advisories} as a factory. New advisories must be registered there!
   */
  private void registerAdvisories() {
    Map<String, Advisory> advisories = Advisories.create(plugin);

    for (Map.Entry<String, Advisory> entry : advisories.entrySet()) {
      registerAdvisory(entry.getKey(), entry.getValue());
    }

    // load them into memory
    List<String> dismissed = advisorData.getStringList("dismissed");
    dismissedAdvisories.addAll(dismissed);
  }


  @Override
  public void onRegister() {
    // save advisor data file if not present
    if (!advisorDataFile.exists())
      plugin.saveResource("advisor_data.yml", false);

    registerAdvisories();


  }

  /* ====== Command Definitions =======  */

  private Map<String, Advisory> getAdvisories() {
    return advisories;
  }

  private Set<String> getDismissedAdvisories() {
    return dismissedAdvisories;
  }

  @SuppressWarnings("UnstableApiUsage")
  private Advisory findAdvisory(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
    // custom error
    DynamicCommandExceptionType ERROR_INVALID_ADVISORY = new DynamicCommandExceptionType(advisory ->
        MessageComponentSerializer.message().serialize(
            text(advisory + " is not a valid module.")
        )
    );

    // try to get the advisory from this object
    String advisoryId = StringArgumentType.getString(ctx, "advisoryId");

    @Nullable Advisory advisory = getAdvisories().get(advisoryId);
    // not found
    if (advisory == null) throw ERROR_INVALID_ADVISORY.create(advisoryId);

    // found
    return advisory;
  }

  @SuppressWarnings("UnstableApiUsage")
  private RequiredArgumentBuilder<CommandSourceStack, String> advisoryArgument() {
    // advisory argument resolver
    return Commands.argument("advisoryId", StringArgumentType.word())
        .suggests((ctx, builder) -> {
          getAdvisories().keySet().forEach(builder::suggest);
          return builder.buildFuture();
        });
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> getCommandNode(@NotNull String name) {


    return Commands.literal(name)
        .requires(ctx -> ctx.getSender().hasPermission("szlaban.%s".formatted(name)))
        .requires(ctx -> isEnabled())
        .then(Commands.literal("dismiss")
            .then(advisoryArgument()
                .executes(ctx -> {
                  // check if the advisory is valid
                  Advisory advisory = findAdvisory(ctx);
                  String advisoryId = StringArgumentType.getString(ctx, "advisoryId");

                  dismissAdvisory(advisoryId);

                  ctx.getSource().getSender().sendRichMessage("<gold>Advisory <yellow>" + advisory.getName() + "</yellow> has been dismissed.");

                  return Command.SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("resolve")
            .then(advisoryArgument()
                .executes(ctx -> {
                  Advisory advisory = findAdvisory(ctx);

                  advisory.resolve();

                  ctx.getSource().getSender().sendRichMessage("<gold>Advisory <yellow>" + advisory.getName() + "</yellow> has been resolved.");

                  return Command.SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("info")
            .then(advisoryArgument()
                .executes(ctx -> {
                  Advisory advisory = findAdvisory(ctx);
                  String id = StringArgumentType.getString(ctx, "advisoryId");
                  Audience audience = ctx.getSource().getSender();

                  audience.sendMessage(
                      text("━".repeat(advisory.getName().length()), NamedTextColor.DARK_GRAY)
                  );
                  audience.sendMessage(
                      text(advisory.getName(), NamedTextColor.GOLD)
                          .decorate(TextDecoration.BOLD)
                          .append(text(" [" + (advisory.isApplied() ? "✔" : "✘") + "]",
                              advisory.isApplied() ? NamedTextColor.GREEN : NamedTextColor.RED))
                  );
                  audience.sendMessage(advisory.getDescription().asComponent().color(NamedTextColor.YELLOW));
                  audience.sendMessage(Component.empty());
                  audience.sendMessage(advisory.getReason().asComponent().color(NamedTextColor.GRAY));
                  audience.sendMessage(Component.empty());

                  // dynamically generate actions based on advisory state
                  //noinspection ExtractMethodRecommender
                  boolean isApplied = advisory.isApplied();
                  boolean isDismissed = getDismissedAdvisories().contains(id);

                  Component actions = Component.empty();
                  if (!isApplied) {
                    actions = actions.append(
                        text("[Resolve]", NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("/szlaban %s resolve ".formatted(name) + id))
                            .hoverEvent(HoverEvent.showText(text("Apply the recommended setting")))
                    );
                  }
                  if (!isDismissed) {
                    if (!isApplied) actions = actions.appendSpace();
                    actions = actions.append(
                        text("[Dismiss]", NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.runCommand("/szlaban %s dismiss ".formatted(name) + id))
                            .hoverEvent(HoverEvent.showText(text("Hide this advisory")))
                    );
                  }
                  if (!actions.equals(Component.empty())) audience.sendMessage(actions);


                  return Command.SINGLE_SUCCESS;
                })
            )
        )
        .then(Commands.literal("list")
            .executes(ctx -> {
              Component list = text("The following advisories are available:", NamedTextColor.GOLD)
                  .appendNewline();

              for (Map.Entry<String, Advisory> entry : getAdvisories().entrySet()) {
                String id = entry.getKey();
                Advisory advisory = entry.getValue();

                TextColor colour;
                if (dismissedAdvisories.contains(id)) { // dismissed
                  colour = NamedTextColor.GRAY;
                } else if (!advisory.isApplied()) { // needs applied
                  colour = NamedTextColor.RED;
                } else {
                  colour = NamedTextColor.GREEN;
                }

                list = list.append(
                    text("• " + advisory.getName(), colour)
                        .hoverEvent(HoverEvent.showText(advisory.getDescription()))
                        .clickEvent(ClickEvent.runCommand("/szlaban %s info ".formatted(name) + id))
                ).append(Component.newline());
              }

              // remove last newline
              list = list.children().removeLast();

              ctx.getSource().getSender().sendMessage(list);

              return Command.SINGLE_SUCCESS;
            })
        )
        .build();
  }
}
/*
File file = new File(plugin.getDataFolder(), "items.yml");
YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
// Work with config here
config.save(file);

 */
