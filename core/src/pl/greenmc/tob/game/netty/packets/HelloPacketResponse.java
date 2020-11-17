package pl.greenmc.tob.game.netty.packets;

/**
 * Response for {@link HelloPacket}
 */
public class HelloPacketResponse {
    private final byte[] challengeData;

    /**
     * @param challengeData Challenge data
     */
    public HelloPacketResponse(byte[] challengeData) {
        this.challengeData = challengeData;
    }

    /**
     * @return Challenge data
     */
    public byte[] getChallengeData() {
        return challengeData;
    }
}
