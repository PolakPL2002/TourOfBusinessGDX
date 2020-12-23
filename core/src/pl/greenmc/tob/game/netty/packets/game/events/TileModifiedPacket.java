package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class TileModifiedPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_TILE_MODIFIED";
    private final int level;
    private final Integer owner;
    private final int tile;

    public TileModifiedPacket(int tile, Integer owner, int level) {
        this.tile = tile;
        this.owner = owner;
        this.level = level;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public TileModifiedPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement tile = objectToDecode.get("tile");
                    if (tile != null && tile.isJsonPrimitive()) this.tile = tile.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement owner = objectToDecode.get("owner");
                    if (owner != null) {
                        if (owner.isJsonPrimitive()) this.owner = owner.getAsInt();
                        else if (owner.isJsonNull()) this.owner = null;
                        else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();

                    JsonElement level = objectToDecode.get("level");
                    if (level != null && level.isJsonPrimitive()) this.level = level.getAsInt();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int getLevel() {
        return level;
    }

    public Integer getOwner() {
        return owner;
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
        out.addProperty("owner", owner);
        out.addProperty("level", level);
        return out;
    }
}
