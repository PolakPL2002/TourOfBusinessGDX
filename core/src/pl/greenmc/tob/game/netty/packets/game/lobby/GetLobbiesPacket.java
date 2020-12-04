package pl.greenmc.tob.game.netty.packets.game.lobby;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.Player;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class GetLobbiesPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_LOBBY_GET_LOBBIES";

    public GetLobbiesPacket() {

    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public GetLobbiesPacket(JsonObject objectToDecode) throws InvalidPacketException {
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
    public static JsonObject generateResponse(@NotNull Lobby[] lobbies, @NotNull Player[] players) {
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (Lobby lobby : lobbies) {
            array.add(lobby.toJsonObject());
        }
        response.add("lobbies", array);
        array = new JsonArray();
        for (Player player : players) {
            array.add(player.toJsonObject());
        }
        response.add("players", array);
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    @NotNull
    @Contract("_ -> new")
    public static GetLobbiesResponse parseResponse(@NotNull JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement lobbies = response.get("lobbies");
        if (lobbies == null || !lobbies.isJsonArray())
            throw new InvalidPacketException();
        JsonArray jsonArray = lobbies.getAsJsonArray();
        Lobby[] out = new Lobby[jsonArray.size()];
        for (int i = 0; i < out.length; i++) {
            final JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement == null || !jsonElement.isJsonObject())
                throw new InvalidPacketException();
            out[i] = new Lobby(jsonElement.getAsJsonObject());
        }

        JsonElement players = response.get("players");
        if (players == null || !players.isJsonArray())
            throw new InvalidPacketException();
        jsonArray = players.getAsJsonArray();
        Player[] outPlayers = new Player[jsonArray.size()];
        for (int i = 0; i < outPlayers.length; i++) {
            final JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement == null || !jsonElement.isJsonObject())
                throw new InvalidPacketException();
            outPlayers[i] = new Player(jsonElement.getAsJsonObject());
        }

        return new GetLobbiesResponse(out, outPlayers);
    }
}
