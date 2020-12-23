package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class CreateLobbyPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_LOBBY_CREATE_LOBBY";

    public CreateLobbyPacket() {

    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public CreateLobbyPacket(JsonObject objectToDecode) throws InvalidPacketException {
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
    public static JsonObject generateResponse(boolean success, int lobbyID) {
        JsonObject response = new JsonObject();
        response.addProperty("success", success);
        response.addProperty("lobbyID", lobbyID);
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @NotNull
    @Contract("_ -> new")
    public static CreateLobbyResponse parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement success = response.get("success");
        if (success == null || !success.isJsonPrimitive())
            throw new InvalidPacketException();
        JsonElement lobbyID = response.get("lobbyID");
        if (lobbyID == null || !lobbyID.isJsonPrimitive())
            throw new InvalidPacketException();

        return new CreateLobbyResponse(success.getAsBoolean(), lobbyID.getAsInt());
    }
}
