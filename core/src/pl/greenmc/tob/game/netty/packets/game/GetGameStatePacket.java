package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class GetGameStatePacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_EVENTS_GAME_STATE_CHANGED";

    public GetGameStatePacket() {

    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public GetGameStatePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type == null || !type.isJsonPrimitive() || !type.getAsString().equalsIgnoreCase(TYPE)) {
                    throw new InvalidPacketException();
                }
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
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
        return out;
    }

    @NotNull
    public static JsonObject generateResponse(@Nullable GameState.Data state) {
        JsonObject response = new JsonObject();
        if (state == null)
            response.add("state", null);
        else
            response.add("state", state.toJsonObject());
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @Nullable
    public static GameState.Data parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement state = response.get("state");
        if (state != null) {
            if (state.isJsonObject())
                return new GameState.Data(state.getAsJsonObject());
            else if (state.isJsonNull())
                return null;
            else throw new InvalidPacketException();
        } else throw new InvalidPacketException();
    }
}
