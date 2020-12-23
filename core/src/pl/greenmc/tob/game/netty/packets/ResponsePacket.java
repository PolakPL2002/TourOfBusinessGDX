package pl.greenmc.tob.game.netty.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.UUID;

/**
 * Packet used to confirm arrival and return response data for other packets.
 */
public class ResponsePacket extends ConfirmationPacket {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "RESPONSE";
    private final JsonObject response;

    /**
     * @param uuid          UUID of message to confirm
     * @param success       Whether packet arrived successfully (checksum match etc.)
     * @param authenticated Whether client is authenticated
     * @param response      Response data for given request
     */
    public ResponsePacket(UUID uuid, boolean success, boolean authenticated, JsonObject response) {
        super(uuid, success, authenticated);
        this.response = response;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public ResponsePacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement response = objectToDecode.get("response");
                    if (response != null && response.isJsonPrimitive())
                        this.response = JsonParser.parseString(response.getAsString()).getAsJsonObject();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    /**
     * @return Response for given request
     */
    public JsonObject getResponse() {
        return response;
    }

    /**
     * Encodes packet for network transmission
     *
     * @return JSON encoded
     */
    @Override
    public JsonObject encode() {
        JsonObject out = super.encode();
        out.remove("type"); //Change type
        out.addProperty("type", TYPE);
        out.addProperty("response", response.toString());
        return out;
    }
}
