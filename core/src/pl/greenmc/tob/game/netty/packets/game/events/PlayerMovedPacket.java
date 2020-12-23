package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class PlayerMovedPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_PLAYER_MOVED";
    private final int player;
    private final int position;
    private final boolean animate;

    public PlayerMovedPacket(int player, int position, boolean animate) {
        this.player = player;
        this.position = position;
        this.animate = animate;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public PlayerMovedPacket(JsonObject objectToDecode) throws InvalidPacketException {
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

                    JsonElement position = objectToDecode.get("position");
                    if (position != null && position.isJsonPrimitive()) this.position = position.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement animate = objectToDecode.get("animate");
                    if (animate != null && animate.isJsonPrimitive()) this.animate = animate.getAsBoolean();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public boolean isAnimated() {
        return animate;
    }

    public int getPlayer() {
        return player;
    }

    public int getPosition() {
        return position;
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
        out.addProperty("position", position);
        out.addProperty("animate", animate);
        return out;
    }
}
