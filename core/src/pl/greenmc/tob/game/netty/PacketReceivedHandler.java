package pl.greenmc.tob.game.netty;

import pl.greenmc.tob.game.netty.packets.Packet;

import javax.annotation.Nullable;

public abstract class PacketReceivedHandler {
    public abstract void onPacketReceived(Packet packet, @Nullable String identity);
}
