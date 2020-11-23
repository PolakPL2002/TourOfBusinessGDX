package pl.greenmc.tob.game.netty.packets.game.lobby;

import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.Player;

public class GetLobbiesResponse {
    private final Lobby[] lobbies;
    private final Player[] players;

    public GetLobbiesResponse(Lobby[] lobbies, Player[] players) {
        this.lobbies = lobbies;
        this.players = players;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Lobby[] getLobbies() {
        return lobbies;
    }
}
