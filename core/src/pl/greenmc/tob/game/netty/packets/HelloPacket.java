package pl.greenmc.tob.game.netty.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;

import java.util.Base64;

/**
 * Packet used to identify server
 */
public class HelloPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "HELLO";
    private final String clientID;

    /**
     * @param clientID Client ID
     */
    public HelloPacket(String clientID) {
        this.clientID = clientID;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public HelloPacket(JsonObject objectToDecode) throws InvalidPacketException {
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

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
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
        return out;
    }

    /**
     * @param challengeData Challenge data
     * @return Response
     */
    public static JsonObject generateResponse(byte[] challengeData) {
        JsonObject response = new JsonObject();
        response.addProperty("challenge", Base64.getEncoder().encodeToString(challengeData));
        return response;
    }

    /**
     * @param response Response to be parsed
     * @return Response data
     * @throws InvalidPacketException On invalid data provided
     */
    public static HelloPacketResponse parseResponse(JsonObject response) throws InvalidPacketException {
        //Decode values
        JsonElement challenge = response.get("challenge");
        byte[] challengeData;
        if (challenge != null && challenge.isJsonPrimitive())
            challengeData = Base64.getDecoder().decode(challenge.getAsString());
        else throw new InvalidPacketException();

        return new HelloPacketResponse(challengeData);
    }
}