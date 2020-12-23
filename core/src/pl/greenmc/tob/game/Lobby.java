package pl.greenmc.tob.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.DefaultMap;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.ArrayList;
import java.util.HashMap;

public class Lobby {
    public static String TYPE = "LOBBY";
    private final int ID;
    private final int owner;
    private final ArrayList<Integer> players = new ArrayList<>();
    private final HashMap<Integer, Boolean> playersReady = new HashMap<>();
    private GameState gameState = null;
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
                    if (owner != null && owner.isJsonPrimitive()) this.owner = owner.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement lobbyState = jsonObject.get("lobbyState");
                    if (lobbyState != null && lobbyState.isJsonPrimitive())
                        this.lobbyState = LobbyState.valueOf(lobbyState.getAsString());
                    else throw new InvalidPacketException();

                    JsonElement players = jsonObject.get("players");
                    if (players != null && players.isJsonArray())
                        for (JsonElement player : players.getAsJsonArray())
                            if (player != null && player.isJsonObject()) {
                                JsonObject p = player.getAsJsonObject();

                                JsonElement id = p.get("id");
                                if (id != null && id.isJsonPrimitive()) this.players.add(id.getAsInt());
                                else throw new InvalidPacketException();

                                JsonElement ready = p.get("ready");
                                if (ready != null && ready.isJsonPrimitive())
                                    playersReady.put(id.getAsInt(), ready.getAsBoolean());
                                else throw new InvalidPacketException();
                            } else throw new InvalidPacketException();
                    else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getID() {
        return ID;
    }

    public int getOwner() {
        return owner;
    }

    public void addPlayer(int playerID) {
        if (!players.contains(playerID)) players.add(playerID);
        playersReady.put(playerID, false);
    }

    public boolean isPlayerReady(int playerID) {
        return playersReady.getOrDefault(playerID, false);
    }

    public void removePlayer(@NotNull Integer playerID) {
        if (players.contains(playerID)) {
            players.remove(playerID);
            playersReady.remove(playerID);
        }
    }

    public void setPlayerReady(int playerID, boolean isReady) {
        if (playersReady.containsKey(playerID))
            playersReady.put(playerID, isReady);
    }

    public LobbyState getLobbyState() {
        return lobbyState;
    }

    public void setLobbyState(LobbyState lobbyState) {
        this.lobbyState = lobbyState;
        if (lobbyState == LobbyState.IN_GAME) {
            Integer[] players = new Integer[this.players.size() + 1];
            players[0] = owner;
            System.arraycopy(getPlayers(), 0, players, 1, players.length - 1);
            GameState.GameSettings gameSettings = new GameState.GameSettings();
            gameSettings.setPriceModifier(10f);
            gameSettings.setEventMoneyMultiplier(1.337f);
            gameState = new GameState(players, new DefaultMap(), () -> setLobbyState(LobbyState.ENDED), gameSettings); //TODO Allow for other maps
            gameState.startTicking();
        }
    }

    public Integer[] getPlayers() {
        return players.toArray(new Integer[0]);
    }

    @NotNull
    public JsonObject toJsonObject() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("ID", ID);
        out.addProperty("owner", owner);
        out.addProperty("lobbyState", lobbyState.name());
        JsonArray players = new JsonArray();
        for (int player : this.players) {
            JsonObject p = new JsonObject();
            p.addProperty("id", player);
            p.addProperty("ready", playersReady.getOrDefault(player, false));
            players.add(p);
        }
        out.add("players", players);
        return out;
    }

    public boolean allReady() {
        for (int player : players)
            if (!playersReady.getOrDefault(player, false))
                return false;
        return true;
    }

    public enum LobbyState {
        CONFIGURING,
        IN_GAME,
        ENDED
    }

}
