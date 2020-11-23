package pl.greenmc.tob.game.netty.packets.game.events.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class PlayerReadyStateChanged extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_EVENTS_LOBBY_PLAYER_READY_STATE_CHANGED";
    private final int playerID;
    private final boolean ready;

    public PlayerReadyStateChanged(int playerID, boolean ready) {
        this.playerID = playerID;
        this.ready = ready;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public PlayerReadyStateChanged(JsonObject objectToDecode) throws InvalidPacketException {
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
                    JsonElement ready = objectToDecode.get("ready");
                    if (ready != null && ready.isJsonPrimitive()) this.ready = ready.getAsBoolean();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public boolean isReady() {
        return ready;
    }

    public int getPlayerID() {
        return playerID;
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
        out.addProperty("ready", ready);
        return out;
    }
}
