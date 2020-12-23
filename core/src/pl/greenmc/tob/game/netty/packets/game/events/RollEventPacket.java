package pl.greenmc.tob.game.netty.packets.game.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class RollEventPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static final String TYPE = "GAME_EVENTS_ROLL";
    private final int[] numbers;
    private final int player;

    public RollEventPacket(int player, int[] numbers) {
        this.player = player;
        this.numbers = numbers;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public RollEventPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement player = objectToDecode.get("player");
                    if (player != null && player.isJsonPrimitive()) this.player = player.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement numbers = objectToDecode.get("numbers");
                    if (numbers != null && numbers.isJsonArray()) {
                        int i = 0;
                        JsonArray array = numbers.getAsJsonArray();
                        this.numbers = new int[array.size()];
                        for (JsonElement number : array)
                            if (number != null && number.isJsonPrimitive()) {
                                this.numbers[i] = number.getAsInt();
                                i++;
                            } else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int[] getNumbers() {
        return numbers;
    }

    public int getPlayer() {
        return player;
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
        out.addProperty("player", player);
        JsonArray numbers = new JsonArray();
        for (int number : this.numbers) {
            numbers.add(number);
        }
        out.add("numbers", numbers);
        return out;
    }
}