package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class PayEventPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_EVENTS_PAY";
    private final long amount;
    private final Integer from;
    private final Integer to;

    public PayEventPacket(Integer from, Integer to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public PayEventPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement to = objectToDecode.get("to");
                    if (to != null) {
                        if (to.isJsonPrimitive()) this.to = to.getAsInt();
                        else if (to.isJsonNull()) this.to = null;
                        else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();

                    JsonElement from = objectToDecode.get("from");
                    if (from != null) {
                        if (from.isJsonPrimitive()) this.from = from.getAsInt();
                        else if (from.isJsonNull()) this.from = null;
                        else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();

                    JsonElement amount = objectToDecode.get("amount");
                    if (amount != null && amount.isJsonPrimitive()) this.amount = amount.getAsLong();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public long getAmount() {
        return amount;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
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
        out.addProperty("from", from);
        out.addProperty("to", to);
        out.addProperty("amount", amount);
        return out;
    }
}
