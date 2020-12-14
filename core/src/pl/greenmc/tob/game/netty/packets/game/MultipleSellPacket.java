package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class MultipleSellPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_MULTIPLE_SELL";
    private final int[] tiles;

    /**
     * Default constructor
     */
    public MultipleSellPacket(int[] tiles) {
        this.tiles = tiles;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public MultipleSellPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                    throw new InvalidPacketException();
                }

                JsonElement tiles = objectToDecode.get("tiles");
                if (tiles == null || !tiles.isJsonArray())
                    throw new InvalidPacketException();
                JsonArray jsonArray = tiles.getAsJsonArray();
                this.tiles = new int[jsonArray.size()];
                for (int i = 0; i < this.tiles.length; i++) {
                    final JsonElement jsonElement = jsonArray.get(i);
                    if (jsonElement == null || !jsonElement.isJsonPrimitive())
                        throw new InvalidPacketException();
                    this.tiles[i] = jsonElement.getAsInt();
                }
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int[] getTiles() {
        return tiles;
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
        JsonArray array = new JsonArray();
        for (int tile : tiles) {
            array.add(tile);
        }
        out.add("tiles", array);
        return out;
    }
}
