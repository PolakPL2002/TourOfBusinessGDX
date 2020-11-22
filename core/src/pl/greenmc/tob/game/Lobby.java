package pl.greenmc.tob.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.ArrayList;

//TODO Owner, players, state, gamestate
public class Lobby {
    public static String TYPE = "LOBBY";
    private final int ID;
    private final int owner;
    private final ArrayList<Integer> players = new ArrayList<>();
    private LobbyState lobbyState = LobbyState.CONFIGURING;

    public Lobby(int ID, int owner) {
        this.ID = ID;
        this.owner = owner;
    }

    public Lobby(@NotNull JsonObject jsonObject) throws InvalidPacketException {
        if (jsonObject.has("type")) {
            //Check type
            try {
                JsonElement type = jsonObject.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement ID = jsonObject.get("ID");
                    if (ID != null && ID.isJsonPrimitive()) this.ID = ID.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement owner = jsonObject.get("owner");
                    if (owner != null && owner.isJsonPrimitive()) this.owner = ID.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement lobbyState = jsonObject.get("lobbyState");
                    if (lobbyState != null && lobbyState.isJsonPrimitive())
                        this.lobbyState = LobbyState.valueOf(lobbyState.getAsString());
                    else throw new InvalidPacketException();

                    JsonElement players = jsonObject.get("players");
                    if (players != null && players.isJsonArray())
                        for (JsonElement player : players.getAsJsonArray())
                            if (player != null && player.isJsonPrimitive())
                                this.players.add(player.getAsInt());
                            else throw new InvalidPacketException();
                    else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int getID() {
        return ID;
    }

    public int getOwner() {
        return owner;
    }

    public Integer[] getPlayers() {
        return players.toArray(new Integer[0]);
    }

    public Lobby addPlayer(int playerID) {
        if (!players.contains(playerID)) players.add(playerID);
        return this;
    }

    public Lobby removePlayer(@NotNull Integer playerID) {
        players.remove(playerID);
        return this;
    }

    public LobbyState getLobbyState() {
        return lobbyState;
    }

    public void setLobbyState(LobbyState lobbyState) {
        this.lobbyState = lobbyState;
    }

    public JsonObject toJsonObject() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("ID", ID);
        out.addProperty("owner", owner);
        out.addProperty("lobbyState", lobbyState.name());
        JsonArray players = new JsonArray();
        for (int player : this.players) players.add(player);
        out.add("players", players);
        return out;
    }

    public enum LobbyState {
        CONFIGURING,
        IN_GAME,
        ENDED
    }

}
