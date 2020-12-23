package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class ImprovePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_IMPROVE";
    private final boolean isUpgrade;
    private final int tile;

    /**
     * Default constructor
     */
    public ImprovePacket(int tile, boolean isUpgrade) {
        this.tile = tile;
        this.isUpgrade = isUpgrade;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public ImprovePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                    throw new InvalidPacketException();
                }

                JsonElement tile = objectToDecode.get("tile");
                if (tile != null && tile.isJsonPrimitive())
                    this.tile = tile.getAsInt();
                else throw new InvalidPacketException();

                JsonElement isUpgrade = objectToDecode.get("isUpgrade");
                if (isUpgrade != null && isUpgrade.isJsonPrimitive())
                    this.isUpgrade = isUpgrade.getAsBoolean();
                else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public boolean isUpgrade() {
        return isUpgrade;
    }

    public int getTile() {
        return tile;
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
        out.addProperty("tile", tile);
        out.addProperty("isUpgrade", isUpgrade);
        return out;
    }
}
