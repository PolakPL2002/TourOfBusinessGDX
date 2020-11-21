package pl.greenmc.tob.game.netty.packets;

/**
 * Response for {@link HelloPacket}
 */
public class HelloResponse {
    private final byte[] challengeData;

    /**
     * @param challengeData Challenge data
     */
    public HelloResponse(byte[] challengeData) {
        this.challengeData = challengeData;
    }

    /**
     * @return Challenge data
     */
    public byte[] getChallengeData() {
        return challengeData;
    }
}
