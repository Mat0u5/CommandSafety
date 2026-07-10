package net.mat0u5.commandsafety.mixin;

//? if <= 1.18 {
/*import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.commandsafety.utils.PlayerUtils;
import net.mat0u5.commandsafety.validator.CommandAnalyzer;
import net.mat0u5.commandsafety.validator.ConfirmationCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandDispatcher.class)
public class CommandDispatcherMixin {
	@Inject(method = "execute(Lcom/mojang/brigadier/ParseResults;)I", at = @At("HEAD"), cancellable = true)
	private void onCommandExecute(ParseResults<CommandSourceStack> parse, CallbackInfoReturnable<Integer> cir) {
		if (ConfirmationCommand.executing) return;
		String command = parse.getReader().getString();
		if (command.startsWith("/")) command = command.replaceFirst("/","");
		final CommandContext<CommandSourceStack> context = parse.getContext().build(command);
		try {
			ServerPlayer player = context.getSource().getPlayerOrException();
			if (CommandAnalyzer.shouldConfirm(command, context)) {
				CommandAnalyzer.sendConfirmationMessage(player, command, context);
				PlayerUtils.playSound(player, SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.BLOCKS, 1, 1);
				cir.setReturnValue(0);
			}
		} catch (Exception e) {}
	}
}
*///?} else {
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(Commands.class)
public class CommandDispatcherMixin {
}
//?}
