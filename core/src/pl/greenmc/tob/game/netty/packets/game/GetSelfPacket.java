package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Player;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class GetSelfPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_GET_SELF";

    public GetSelfPacket() {

    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public GetSelfPacket(JsonObject objectToDecode) throws InvalidPacketException {
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
    public static JsonObject generateResponse(@NotNull Player player) {
        JsonObject response = new JsonObject();
        response.add("player", player.toJsonObject());
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @Nullable
    public static Player parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement player = response.get("player");
        if (player == null)
            throw new InvalidPacketException();
        else if (player.isJsonNull()) return null;
        else if (!player.isJsonObject())
            throw new InvalidPacketException();
        return new Player(player.getAsJsonObject());
    }
}