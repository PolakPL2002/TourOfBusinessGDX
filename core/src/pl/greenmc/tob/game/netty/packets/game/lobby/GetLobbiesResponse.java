package pl.greenmc.tob.game.netty.packets.game.lobby;

import pl.greenmc.tob.game.Lobby;

public class GetLobbiesResponse {
    private final Lobby[] lobbies;

    public GetLobbiesResponse(Lobby[] lobbies) {
        this.lobbies = lobbies;
    }

    public Lobby[] getLobbies() {
        return lobbies;
    }
}
