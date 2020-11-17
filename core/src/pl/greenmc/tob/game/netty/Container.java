package pl.greenmc.tob.game.netty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.packets.Packet;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * Netty transmission packet container
 */
public class Container implements Serializable {
    /**
     * SHA-512 checksum
     */
    @Nullable
    public final String checksum;
    /**
     * Unique message identifier (used for confirmations)
     */
    @NotNull
    public final UUID messageUUID;
    /**
     * Packet class
     */
    @NotNull
    public final String packetClass;
    /**
     * Packet data
     */
    @NotNull
    public final String packetData;

    /**
     * @param packet Packet to be enclosed
     */
    public Container(@NotNull Packet packet) {
        messageUUID = UUID.randomUUID();
        packetClass = packet.getClass().getCanonicalName();
        packetData = packet.encode().toString();
        checksum = checksum(messageUUID.toString() + packetClass + packetData);
    }

    /**
     * @param input Data to be checksummed
     * @return SHA-512 checksum or null on error
     */
    private static @Nullable
    String checksum(@NotNull String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashtext = new StringBuilder(no.toString(16));

            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    /**
     * @param messageUUID Unique message identifier (used for confirmations)
     * @param packetClass Packet class
     * @param packetData  Packet data
     */
    public Container(@NotNull UUID messageUUID, @NotNull String packetClass, @NotNull String packetData) {
        this.messageUUID = messageUUID;
        this.packetClass = packetClass;
        this.packetData = packetData;
        this.checksum = checksum(this.messageUUID.toString() + this.packetClass + this.packetData);
    }

    @Override
    public String toString() {
        return "Container{" +
                "messageUUID=" + messageUUID +
                ", packetPackage='" + packetClass + '\'' +
                ", packetData='" + packetData + '\'' +
                '}';
    }

    /**
     * @return Whether checksum is valid. True is no checksum provided.
     */
    public boolean validateChecksum() {
        return Objects.equals(checksum(this.messageUUID.toString() + this.packetClass + this.packetData), checksum);
    }
}
