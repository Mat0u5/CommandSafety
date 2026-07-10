package net.mat0u5.commandsafety.utils;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
//? if > 1.19.2 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?}

public class PlayerUtils {

    public static void playSound(ServerPlayer player, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        player.connection
                .send(
                        //? if <= 1.14 {
                        new ClientboundSoundPacket(
                                sound, soundSource, player.x, player.y, player.z, volume, pitch
                        )
                        //?} else if <= 1.18 {
                        /*new ClientboundSoundPacket(
                                sound, soundSource, player.getX(), player.getY(), player.getZ(), volume, pitch
                        )
                        *///?} else if <= 1.19.2 {
                        /*new ClientboundSoundPacket(
                                sound, soundSource, player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()
                        )
                        *///?} else {
                        /*new ClientboundSoundPacket(
                                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundSource, player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()
                        )
                        *///?}
                );
    }
}
