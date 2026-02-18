package net.mat0u5.commandsafety.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class PlayerUtils {

    public static void playSound(ServerPlayer player, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        player.connection
                .send(
                        new ClientboundSoundPacket(
                                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundSource, player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()
                        )
                );
    }
}
