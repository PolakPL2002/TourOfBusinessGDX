package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class LobbyPlayerJoinedPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_LOBBY_PLAYER_JOINED";
    private final int playerID;

    public LobbyPlayerJoinedPacket(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public LobbyPlayerJoinedPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement playerID = objectToDecode.get("playerID");
                    if (playerID != null && playerID.isJsonPrimitive()) this.playerID = playerID.getAsInt();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
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
        out.addProperty("playerID", playerID);
        return out;
    }
}
