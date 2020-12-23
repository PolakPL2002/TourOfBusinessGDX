package pl.greenmc.tob.game.netty.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.UUID;

/**
 * Packet used to confirm arrival of other packets.
 */
public class ConfirmationPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "CONFIRMATION";
    private final boolean authenticated;
    private final boolean success;
    private final UUID uuid;

    /**
     * @param uuid          UUID of message to confirm
     * @param success       Whether packet arrived successfully (checksum match etc.)
     * @param authenticated Whether client is authenticated
     */
    public ConfirmationPacket(UUID uuid, boolean success, boolean authenticated) {
        super();
        this.uuid = uuid;
        this.success = success;
        this.authenticated = authenticated;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public ConfirmationPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && (type.getAsString().equalsIgnoreCase(TYPE) || type.getAsString().equalsIgnoreCase(ResponsePacket.TYPE))) {
                    //Decode values
                    JsonElement authenticated = objectToDecode.get("authenticated");
                    if (authenticated != null && authenticated.isJsonPrimitive())
                        this.authenticated = authenticated.getAsBoolean();
                    else throw new InvalidPacketException();

                    JsonElement success = objectToDecode.get("success");
                    if (success != null && success.isJsonPrimitive()) this.success = success.getAsBoolean();
                    else throw new InvalidPacketException();

                    JsonElement uuid = objectToDecode.get("uuid");
                    if (uuid != null && uuid.isJsonPrimitive()) this.uuid = UUID.fromString(uuid.getAsString());
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    /**
     * @return Whether client is authenticated
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * @return Whether packet was processed successfully
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return Packet UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Encodes packet for network transmission
     *
     * @return JSON encoded
     */
    @Override
    public JsonObject encode() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("authenticated", authenticated);
        out.addProperty("success", success);
        out.addProperty("uuid", uuid.toString());
        return out;
    }


    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @return Instance of Packet
     */
    public static ConfirmationPacket decode(JsonObject objectToDecode) {
        return null;
    }
}
