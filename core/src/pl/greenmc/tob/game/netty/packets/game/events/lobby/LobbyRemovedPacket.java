package pl.greenmc.tob.game.netty.packets.game.events.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class LobbyRemovedPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_EVENTS_LOBBY_REMOVED";
    private final int lobbyID;

    public LobbyRemovedPacket(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public LobbyRemovedPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement lobbyID = objectToDecode.get("lobbyID");
                    if (lobbyID != null && lobbyID.isJsonPrimitive()) this.lobbyID = lobbyID.getAsInt();
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
        out.addProperty("lobbyID", lobbyID);
        return out;
    }
}
