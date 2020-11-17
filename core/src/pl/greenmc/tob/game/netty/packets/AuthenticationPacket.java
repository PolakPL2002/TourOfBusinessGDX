package pl.greenmc.tob.game.netty.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.Base64;

/**
 * Packet used to identify to server
 */
public class AuthenticationPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "AUTHENTICATION";
    private final byte[] challenge;
    private final String clientID;
    private final byte[] response;

    /**
     * @param clientID  Client ID
     * @param challenge Challenge data
     * @param response  Response data
     */
    public AuthenticationPacket(String clientID, byte[] challenge, byte[] response) {
        this.clientID = clientID;
        this.challenge = challenge;
        this.response = response;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public AuthenticationPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement clientID = objectToDecode.get("clientID");
                    if (clientID != null && clientID.isJsonPrimitive()) this.clientID = clientID.getAsString();
                    else throw new InvalidPacketException();

                    JsonElement challenge = objectToDecode.get("challenge");
                    if (challenge != null && clientID.isJsonPrimitive())
                        this.challenge = Base64.getDecoder().decode(challenge.getAsString());
                    else throw new InvalidPacketException();

                    JsonElement response = objectToDecode.get("response");
                    if (response != null && clientID.isJsonPrimitive())
                        this.response = Base64.getDecoder().decode(response.getAsString());
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    /**
     * @return Challenge data
     */
    public byte[] getChallenge() {
        return challenge;
    }

    /**
     * @return Response data
     */
    public byte[] getResponse() {
        return response;
    }

    /**
     * @return Client ID
     */
    public String getClientID() {
        return clientID;
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
        out.addProperty("clientID", clientID);
        out.addProperty("challenge", Base64.getEncoder().encodeToString(challenge));
        out.addProperty("response", Base64.getEncoder().encodeToString(response));
        return out;
    }
}
