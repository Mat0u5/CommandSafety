package net.mat0u5.commandsafety.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.commandsafety.utils.PlayerUtils;
import net.mat0u5.commandsafety.validator.CommandAnalyzer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if <= 1.20.2 {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} else {
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 *///?}

import net.mat0u5.commandsafety.validator.ConfirmationCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if > 1.18
//import net.minecraft.commands.CommandBuildContext;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

//? if !forge {
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V"), method = "<init>")
//?} else {
    /*//? if <= 1.20.5 {
    /^@Inject(method = "<init>", at = @At("RETURN"))
    ^///?} else {
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V", unsafe = true), method = "<init>")
    //?}
*///?}
    //? if <= 1.15 {
    private void addCommands(boolean bl, CallbackInfo ci) {
    //?} else if <= 1.18 {
    /*private void addCommands(Commands.CommandSelection selection, CallbackInfo ci) {
    *///?} else {
    /*private void addCommands(Commands.CommandSelection selection, CommandBuildContext buildContext, CallbackInfo ci) {
    *///?}
        ConfirmationCommand.register(this.dispatcher);
    }

    //? if <= 1.18 {
    private void onCommandExecute(CommandSourceStack source, String command, CallbackInfoReturnable<Integer> cir) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ParseResults<CommandSourceStack> parseResults = this.dispatcher.parse(command, source);
            CommandContext<CommandSourceStack> context = parseResults.getContext().build(command);
            if (context == null) return;
    //?} else if <= 1.20.2 {
    /*@Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void onCommandExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfoReturnable<Integer> cir) {
        CommandSourceStack source = parseResults.getContext().getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            CommandContext<CommandSourceStack> context = parseResults.getContext().build(command);
    *///?} else {
    /*@Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void onCommandExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        CommandSourceStack source = parseResults.getContext().getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            CommandContext<CommandSourceStack> context = parseResults.getContext().build(command);
    *///?}
            if (CommandAnalyzer.shouldConfirm(command, context)) {
                CommandAnalyzer.sendConfirmationMessage(player, command, context);
                //? if <= 1.19.2 {
                PlayerUtils.playSound(player, SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.BLOCKS, 1, 1);
                //?} else {
                /*PlayerUtils.playSound(player, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.BLOCKS, 1, 1);
                 *///?}
                //? if <= 1.20.2 {
                cir.setReturnValue(0);
                //?} else {
                /*ci.cancel();
                 *///?}
            }
        }
    }
}
