package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;
import pl.greenmc.tob.graphics.scenes.game.dialogs.trade.TradeDialog;

public class IncomingTradePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_INCOMING_TRADE";
    @NotNull
    private final TradeDialog.Property[] player1Offer;
    @NotNull
    private final TradeDialog.Property[] player2Offer;
    private final int playerID;

    /**
     * Default constructor
     */
    public IncomingTradePacket(int playerID, @NotNull TradeDialog.Property[] player1Offer, @NotNull TradeDialog.Property[] player2Offer) {
        this.playerID = playerID;
        this.player1Offer = player1Offer;
        this.player2Offer = player2Offer;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public IncomingTradePacket(JsonObject objectToDecode) throws InvalidPacketException {
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

                JsonElement player1Offer = objectToDecode.get("player1Offer");
                if (player1Offer == null || !player1Offer.isJsonArray())
                    throw new InvalidPacketException();
                JsonArray jsonArray = player1Offer.getAsJsonArray();
                this.player1Offer = new TradeDialog.Property[jsonArray.size()];
                for (int i = 0; i < this.player1Offer.length; i++) {
                    final JsonElement jsonElement = jsonArray.get(i);
                    if (jsonElement == null || !jsonElement.isJsonObject())
                        throw new InvalidPacketException();
                    this.player1Offer[i] = new TradeDialog.Property(jsonElement.getAsJsonObject());
                }

                JsonElement player2Offer = objectToDecode.get("player2Offer");
                if (player2Offer == null || !player2Offer.isJsonArray())
                    throw new InvalidPacketException();
                jsonArray = player2Offer.getAsJsonArray();
                this.player2Offer = new TradeDialog.Property[jsonArray.size()];
                for (int i = 0; i < this.player2Offer.length; i++) {
                    final JsonElement jsonElement = jsonArray.get(i);
                    if (jsonElement == null || !jsonElement.isJsonObject())
                        throw new InvalidPacketException();
                    this.player2Offer[i] = new TradeDialog.Property(jsonElement.getAsJsonObject());
                }
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    @NotNull
    public TradeDialog.Property[] getPlayer1Offer() {
        return player1Offer;
    }

    @NotNull
    public TradeDialog.Property[] getPlayer2Offer() {
        return player2Offer;
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
        JsonArray array = new JsonArray();
        for (TradeDialog.Property tile : player1Offer) {
            array.add(tile.toJsonObject());
        }
        out.add("player1Offer", array);

        array = new JsonArray();
        for (TradeDialog.Property tile : player2Offer) {
            array.add(tile.toJsonObject());
        }
        out.add("player2Offer", array);
        return out;
    }
}
