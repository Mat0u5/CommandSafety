package net.mat0u5.commandsafety.validator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.mat0u5.commandsafety.Main.config;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ConfirmationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext commandRegistryAccess,
                                Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(
                literal("confirmcmd")
                        .requires(source -> (hasPermission(source) || (source.getEntity() == null)))
                .then(argument("action", StringArgumentType.string())
                        .executes(ConfirmationCommand::execute)
                )
                .then(literal("config")
                        .then(argument("name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("max_players", "max_living_entities", "max_entities", "max_score_holders", "max_blocks"), builder))
                                .executes(context -> getProperty(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "name")
                                ))
                                .then(argument("value", IntegerArgumentType.integer(1))
                                        .executes(context -> setProperty(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                IntegerArgumentType.getInteger(context, "value")
                                        ))
                                )
                        )
                )
        );
    }

    private static boolean hasPermission(CommandSourceStack source) {
        if (source.getPlayer() == null) return false;
        return source.getServer().getPlayerList().isOp(source.getPlayer().nameAndId());
    }

    private static int setProperty(CommandSourceStack source, String name, int value) {
        config.setProperty(name, String.valueOf(value));
        CommandAnalyzer.loadConfig();
        source.sendSystemMessage(Component.nullToEmpty("Set config '" + name + "' to: " + value));
        return 1;
    }

    private static int getProperty(CommandSourceStack source, String name) {
        String property = config.getProperty(name);
        if (property == null) {
            source.sendFailure(Component.nullToEmpty("Config '" + name + "' not found."));
            return 0;
        }
        source.sendSystemMessage(Component.nullToEmpty("Config '" + name + "' is set to: " + property));
        return 1;
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String action = StringArgumentType.getString(context, "action");

        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUUID());

        if (pending == null) {
            player.displayClientMessage(Component.literal("No pending command to confirm.").withStyle(ChatFormatting.RED), false);
            return 0;
        }

        if ("cancel".equals(action)) {
            CommandValidator.removePendingCommand(player.getUUID());
            player.displayClientMessage(Component.literal("Command cancelled.").withStyle(ChatFormatting.GREEN), false);
            return 1;
        }

        if (pending.confirmId.equals(action)) {
            CommandValidator.removePendingCommand(player.getUUID());

            try {
                CommandDispatcher<CommandSourceStack> dispatcher = context.getSource().getServer().getCommands().getDispatcher();
                dispatcher.execute(pending.command, source);
                player.displayClientMessage(Component.literal("Command executed.").withStyle(ChatFormatting.GREEN), false);
                return 1;
            } catch (Exception e) {
                player.displayClientMessage(Component.literal("Failed to execute command: " + e.getMessage()).withStyle(ChatFormatting.RED), false);
                return 0;
            }
        } else {
            player.displayClientMessage(Component.literal("Invalid confirmation code.").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }
}