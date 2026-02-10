package com.sins;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

public final class SinCommands {
    private SinCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("sin")
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                        .then(CommandManager.literal("choose")
                                .then(CommandManager.argument("sin", StringArgumentType.word())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(SinOriginManager.SINS, builder))
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            String sin = StringArgumentType.getString(context, "sin");
                                            return chooseSin(context.getSource(), player, sin);
                                        })))
                        .then(CommandManager.literal("random")
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    return randomSin(context.getSource(), player);
                                }))
        ));
    }

    private static int chooseSin(ServerCommandSource source, ServerPlayerEntity player, String sin) {
        if (!SinOriginManager.assignSin(player, sin)) {
            source.sendError(Text.literal("Could not assign sin '" + sin + "'.").formatted(Formatting.RED));
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Assigned Sin: " + sin.toUpperCase(Locale.ROOT)).formatted(Formatting.GOLD), false);
        return 1;
    }

    private static int randomSin(ServerCommandSource source, ServerPlayerEntity player) {
        return SinOriginManager.assignRandomSin(player)
                .map(sin -> {
                    source.sendFeedback(() -> Text.literal("Assigned random Sin: " + sin.toUpperCase(Locale.ROOT)).formatted(Formatting.GOLD), false);
                    return 1;
                })
                .orElseGet(() -> {
                    source.sendError(Text.literal("Could not assign a random sin.").formatted(Formatting.RED));
                    return 0;
                });
    }
}
