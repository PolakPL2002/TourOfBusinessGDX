package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class TradeResponsePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_TRADE_RESPONSE";
    @NotNull
    private final TradeResponse response;

    /**
     * Default constructor
     */
    public TradeResponsePacket(@NotNull TradeResponse response) {
        this.response = response;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public TradeResponsePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                    throw new InvalidPacketException();
                }

                JsonElement response = objectToDecode.get("response");
                if (response != null && response.isJsonPrimitive())
                    this.response = TradeResponse.valueOf(response.getAsString());
                else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    @NotNull
    public TradeResponse getResponse() {
        return response;
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
        out.addProperty("response", response.name());
        return out;
    }

    public enum TradeResponse {
        ACCEPT,
        REJECT
    }
}