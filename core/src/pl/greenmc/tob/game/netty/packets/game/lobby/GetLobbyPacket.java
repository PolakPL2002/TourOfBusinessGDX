package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.Player;
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

    public int getLobbyID() {
        return lobbyID;
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
    public static JsonObject generateResponse(@Nullable Lobby lobby, @Nullable Player[] players) {
        JsonObject response = new JsonObject();
        if (lobby == null)
            response.add("lobby", null);
        else
            response.add("lobby", lobby.toJsonObject());
        if (players == null)
            response.add("players", null);
        else {
            JsonArray array = new JsonArray();
            for (Player player : players) {
                array.add(player.toJsonObject());
            }
            response.add("players", array);
        }
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @NotNull
    public static GetLobbyResponse parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement lobby = response.get("lobby");
        Lobby lobby1;
        if (lobby == null)
            throw new InvalidPacketException();
        else if (lobby.isJsonNull())
            lobby1 = null;
        else if (!lobby.isJsonObject())
            throw new InvalidPacketException();
        else
            lobby1 = new Lobby(lobby.getAsJsonObject());


        JsonElement players = response.get("players");
        Player[] out;
        if (players == null)
            throw new InvalidPacketException();
        else if (players.isJsonNull())
            out = null;
        else if (!players.isJsonArray())
            throw new InvalidPacketException();
        else {
            final JsonArray jsonArray = players.getAsJsonArray();
            out = new Player[jsonArray.size()];
            for (int i = 0; i < out.length; i++) {
                final JsonElement jsonElement = jsonArray.get(i);
                if (jsonElement == null || !jsonElement.isJsonObject())
                    throw new InvalidPacketException();
                out[i] = new Player(jsonElement.getAsJsonObject());
            }
        }

        return new GetLobbyResponse(lobby1, out);
    }
}
