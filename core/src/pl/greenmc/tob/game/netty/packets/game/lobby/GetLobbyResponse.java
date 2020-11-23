package pl.greenmc.tob.game.netty.packets.game.lobby;

import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.Lobby;
import pl.greenmc.tob.game.Player;

public class GetLobbyResponse {
    @Nullable
    private final Lobby lobby;
    @Nullable
    private final Player[] players;

    public GetLobbyResponse(@Nullable Lobby lobby, @Nullable Player[] players) {
        this.lobby = lobby;
        this.players = players;
    }

    @Nullable
    public Lobby getLobby() {
        return lobby;
    }

    @Nullable
    public Player[] getPlayers() {
        return players;
    }
}
