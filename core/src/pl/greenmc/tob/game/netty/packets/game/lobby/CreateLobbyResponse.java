package pl.greenmc.tob.game.netty.packets.game.lobby;

public class CreateLobbyResponse {
    private final int lobbyID;
    private final boolean success;

    /**
     * @param success Whether lobby was created
     * @param lobbyID ID of created lobby
     */
    public CreateLobbyResponse(boolean success, int lobbyID) {
        this.success = success;
        this.lobbyID = lobbyID;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getLobbyID() {
        return lobbyID;
    }
}
