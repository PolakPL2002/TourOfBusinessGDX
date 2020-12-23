package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class StartGamePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_LOBBY_START_GAME";


    public StartGamePacket() {

    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public StartGamePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type == null || !type.isJsonPrimitive() || !type.getAsString().equalsIgnoreCase(TYPE)) {
                    throw new InvalidPacketException();
                }
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    /**
     * Encodes packet for network transmission
     *
     * @return JSON encoded
     */
    @Override
    public JsonObject encode() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        return out;
    }
}
