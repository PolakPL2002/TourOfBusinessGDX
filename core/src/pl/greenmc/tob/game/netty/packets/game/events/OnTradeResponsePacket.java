package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;
import pl.greenmc.tob.game.netty.packets.game.TradeResponsePacket;

public class OnTradeResponsePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_ON_TRADE_RESPONSE";
    private final int playerID;
    @NotNull
    private final TradeResponsePacket.TradeResponse response;

    /**
     * Default constructor
     */
    public OnTradeResponsePacket(int playerID, @NotNull TradeResponsePacket.TradeResponse response) {
        this.playerID = playerID;
        this.response = response;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public OnTradeResponsePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                    throw new InvalidPacketException();
                }

                JsonElement playerID = objectToDecode.get("playerID");
                if (playerID != null && playerID.isJsonPrimitive())
                    this.playerID = playerID.getAsInt();
                else throw new InvalidPacketException();

                JsonElement response = objectToDecode.get("response");
                if (response != null && response.isJsonPrimitive())
                    this.response = TradeResponsePacket.TradeResponse.valueOf(response.getAsString());
                else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int getPlayerID() {
        return playerID;
    }

    @NotNull
    public TradeResponsePacket.TradeResponse getResponse() {
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
        out.addProperty("playerID", playerID);
        return out;
    }
}