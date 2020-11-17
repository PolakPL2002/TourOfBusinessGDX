package pl.greenmc.tob.game.netty.packets;

import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;

/**
 * Data packet
 */
public abstract class Packet {
    /**
     * Default constructor
     */
    public Packet() {
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    @SuppressWarnings("RedundantThrows")
    public Packet(JsonObject objectToDecode) throws InvalidPacketException {
    }

    /**
     * Encodes packet for network transmission
     *
     * @return JSON encoded
     */
    public abstract JsonObject encode();
}
