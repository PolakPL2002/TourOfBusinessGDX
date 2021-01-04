package pl.greenmc.tob.game.netty.packets;

/**
 * Response for {@link HelloPacket}
 */
public class HelloResponse {
    private final String serverVersion;
    private final byte[] challengeData;

    /**
     * @param serverVersion Server version
     * @param challengeData Challenge data
     */
    public HelloResponse(String serverVersion, byte[] challengeData) {
        this.serverVersion = serverVersion;
        this.challengeData = challengeData;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * @return Challenge data
     */
    public byte[] getChallengeData() {
        return challengeData;
    }
}
