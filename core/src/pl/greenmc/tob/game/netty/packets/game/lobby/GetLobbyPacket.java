package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class GetLobbyPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_LOBBY_GET_LOBBY";
    private final int lobbyID;

    public GetLobbyPacket(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public GetLobbyPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement lobbyID = objectToDecode.get("lobbyID");
                    if (lobbyID != null && lobbyID.isJsonPrimitive()) this.lobbyID = lobbyID.getAsInt();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
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
        out.addProperty("lobbyID", lobbyID);
        return out;
    }

    @NotNull
    public static JsonObject generateResponse(@NotNull Lobby lobby) {
        JsonObject response = new JsonObject();
        response.add("lobby", lobby.toJsonObject());
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @Nullable
    public static Lobby parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement lobby = response.get("lobby");
        if (lobby == null) return null;
        if (!lobby.isJsonObject())
            throw new InvalidPacketException();
        return new Lobby(lobby.getAsJsonObject());
    }
}
