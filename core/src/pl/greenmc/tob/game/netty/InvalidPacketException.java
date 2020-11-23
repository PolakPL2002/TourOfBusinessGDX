package pl.greenmc.tob.game.netty;

/**
 * Thrown when packet data is invalid
 */
public class InvalidPacketException extends Exception {
    public InvalidPacketException() {
        super("Packet is invalid.");
    }
}
