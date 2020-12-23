package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Card;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class CardGrantedEvent extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_CARD_GRANTED";
    @NotNull
    private final Card card;
    private final int player;

    public CardGrantedEvent(int player, @NotNull Card card) {
        this.player = player;
        this.card = card;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public CardGrantedEvent(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement player = objectToDecode.get("player");
                    if (player != null && player.isJsonPrimitive()) this.player = player.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement card = objectToDecode.get("card");
                    if (card != null && card.isJsonObject()) this.card = new Card(card.getAsJsonObject());
                    else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    @NotNull
    public Card getCard() {
        return card;
    }

    public int getPlayer() {
        return player;
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
        out.addProperty("player", player);
        out.add("card", card.toJsonObject());
        return out;
    }
}
