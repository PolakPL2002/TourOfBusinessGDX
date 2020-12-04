package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class PlayerStateChangedPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_EVENTS_PLAYER_STATE_CHANGED";
    private final int player;
    private final GameState.PlayerState state;

    public PlayerStateChangedPacket(int player, @NotNull GameState.PlayerState state) {
        this.player = player;
        this.state = state;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public PlayerStateChangedPacket(JsonObject objectToDecode) throws InvalidPacketException {
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

                    JsonElement state = objectToDecode.get("state");
                    if (state != null && state.isJsonObject())
                        this.state = new GameState.PlayerState(state.getAsJsonObject());
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int getPlayer() {
        return player;
    }

    @NotNull
    public GameState.PlayerState getState() {
        return state;
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
        out.add("state", state.toJsonObject());
        return out;
    }
}
