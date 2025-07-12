package net.mat0u5.commandsafety.validator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.mat0u5.commandsafety.Main.config;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfirmationCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                literal("confirmcmd")
                        .requires(source -> (hasPermission(source) || (source.getEntity() == null)))
                .then(argument("action", StringArgumentType.string())
                        .executes(ConfirmationCommand::execute)
                )
                .then(literal("config")
                        .then(argument("name", StringArgumentType.string())
                                .suggests((context, builder) -> CommandSource.suggestMatching(List.of("max_players", "max_living_entities", "max_entities", "max_score_holders", "max_blocks"), builder))
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

    private static boolean hasPermission(ServerCommandSource source) {
        if (source.getPlayer() == null) return false;
        return source.getServer().getPlayerManager().isOperator(source.getPlayer().getGameProfile());
    }

    private static int setProperty(ServerCommandSource source, String name, int value) {
        config.setProperty(name, String.valueOf(value));
        CommandAnalyzer.loadConfig();
        source.sendMessage(Text.of("Set config '" + name + "' to: " + value));
        return 1;
    }

    private static int getProperty(ServerCommandSource source, String name) {
        String property = config.getProperty(name);
        if (property == null) {
            source.sendError(Text.of("Config '" + name + "' not found."));
            return 0;
        }
        source.sendMessage(Text.of("Config '" + name + "' is set to: " + property));
        return 1;
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String action = StringArgumentType.getString(context, "action");

        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUuid());

        if (pending == null) {
            player.sendMessage(Text.literal("No pending command to confirm.").formatted(Formatting.RED), false);
            return 0;
        }

        if ("cancel".equals(action)) {
            CommandValidator.removePendingCommand(player.getUuid());
            player.sendMessage(Text.literal("Command cancelled.").formatted(Formatting.GREEN), false);
            return 1;
        }

        if (pending.confirmId.equals(action)) {
            CommandValidator.removePendingCommand(player.getUuid());

            try {
                CommandDispatcher<ServerCommandSource> dispatcher = context.getSource().getServer().getCommandManager().getDispatcher();
                dispatcher.execute(pending.command, source);
                player.sendMessage(Text.literal("Command executed.").formatted(Formatting.GREEN), false);
                return 1;
            } catch (Exception e) {
                player.sendMessage(Text.literal("Failed to execute command: " + e.getMessage()).formatted(Formatting.RED), false);
                return 0;
            }
        } else {
            player.sendMessage(Text.literal("Invalid confirmation code.").formatted(Formatting.RED), false);
            return 0;
        }
    }
}