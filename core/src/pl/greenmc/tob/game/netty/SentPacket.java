package pl.greenmc.tob.game.netty;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.packets.Packet;

import java.util.UUID;

import static pl.greenmc.tob.game.util.Logger.warning;

/**
 * Stores information about sent packet
 */
public class SentPacket {
    @Nullable
    private final Callback callback;
    private final Packet packet;
    private final boolean resendOnFailure;
    private final UUID uuid;
    private boolean failed = false;
    private boolean succeeded = false;

    /**
     * @param packet          Packet
     * @param callback        Callback
     * @param resendOnFailure Should packet be resend on failure
     * @param uuid            Packet UUID
     */
    public SentPacket(Packet packet, @Nullable Callback callback, boolean resendOnFailure, UUID uuid) {
        this.packet = packet;
        this.callback = callback;
        this.resendOnFailure = resendOnFailure;
        this.uuid = uuid;
    }

    /**
     * @return Whether packet succeeded
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * @return Whether packet failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * @return Callback
     */
    public @Nullable
    Callback getCallback() {
        return callback;
    }

    /**
     * @return Packet
     */
    public Packet getPacket() {
        return packet;
    }

    /**
     * @return Should packet be resend on failure
     */
    public boolean isResendOnFailure() {
        return resendOnFailure;
    }

    /**
     * @return Packet UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @param response Response for packet
     */
    public void success(@Nullable JsonObject response) {
        if (!gotResponse() && callback != null) callback.success(uuid, response);
        if (!gotResponse()) succeeded = true;
    }

    /**
     * @return Whether packet either succeeded or failed
     */
    public boolean gotResponse() {
        return succeeded || failed;
    }

    /**
     * @param reason Reason why packet failed
     */
    public void failure(@NotNull FailureReason reason) {
        if (!gotResponse() && callback != null) callback.failure(uuid, reason);
        if (!gotResponse()) failed = true;
    }

    /**
     * Reason why packet failed
     */
    public enum FailureReason {
        /**
         * Response received had invalid checksum
         */
        CORRUPTED_RESPONSE,
        /**
         * Response timed out
         */
        TIMEOUT,
        /**
         * Client is not authenticated
         */
        NOT_AUTHENTICATED
    }

    /**
     * Callback for conformation/response
     */
    public abstract static class Callback {
        /**
         * @param uuid     Packet UUID
         * @param response Response for packet
         */
        public abstract void success(@NotNull UUID uuid, @Nullable JsonObject response);

        /**
         * @param uuid   Packet UUID
         * @param reason Reason why packet failed
         */
        public abstract void failure(@NotNull UUID uuid, @NotNull FailureReason reason);

        public static class BlankCallback extends Callback {
            /**
             * @param uuid     Packet UUID
             * @param response Response for packet
             */
            @Override
            public void success(@NotNull UUID uuid, @Nullable JsonObject response) {

            }

            /**
             * @param uuid   Packet UUID
             * @param reason Reason why packet failed
             */
            @Override
            public void failure(@NotNull UUID uuid, @NotNull FailureReason reason) {
                warning("Request failed: " + reason);
            }
        }
    }
}
