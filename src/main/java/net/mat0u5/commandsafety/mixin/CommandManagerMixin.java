package net.mat0u5.commandsafety.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.commandsafety.validator.CommandAnalyzer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void onCommandExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) {
        ServerCommandSource source = parseResults.getContext().getSource();
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            CommandContext<ServerCommandSource> context = parseResults.getContext().build(command);
            if (CommandAnalyzer.shouldConfirm(command, context)) {
                CommandAnalyzer.sendConfirmationMessage(player, command, context);
                player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), SoundCategory.BLOCKS, 1, 1);
                ci.cancel();
            }
        }
    }
}
