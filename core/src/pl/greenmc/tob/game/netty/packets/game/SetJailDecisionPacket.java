package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class SetJailDecisionPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_SET_BUY_DECISION";
    private final GameState.JailDecision decision;

    public SetJailDecisionPacket(GameState.JailDecision decision) {
        this.decision = decision;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public SetJailDecisionPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement decision = objectToDecode.get("decision");
                    if (decision != null && decision.isJsonPrimitive())
                        this.decision = GameState.JailDecision.valueOf(decision.getAsString());
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public GameState.JailDecision getDecision() {
        return decision;
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
        out.addProperty("decision", decision.name());
        return out;
    }
}