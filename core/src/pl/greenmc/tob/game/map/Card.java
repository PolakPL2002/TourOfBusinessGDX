package pl.greenmc.tob.game.map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;

public class Card {
    public static final String TYPE = "CARD";
    private final String description;
    private final String name;
    private final CardType type;
    private final String value;

    public Card(String name, String description, CardType type) {
        this(name, description, type, "");
    }

    public Card(String name, String description, CardType type, @NotNull String value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public Card(String name, String description, CardType type, long value) {
        this(name, description, type, String.valueOf(value));
    }


    public Card(String name, String description, CardType type, @NotNull Tile.TileType value) {
        this(name, description, type, value.name());
    }

    public Card(@NotNull JsonObject jsonObject) throws InvalidPacketException {
        if (jsonObject.has("type")) {
            //Check type
            try {
                JsonElement type = jsonObject.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement name = jsonObject.get("name");
                    if (name != null && name.isJsonPrimitive())
                        this.name = name.getAsString();
                    else throw new InvalidPacketException();

                    JsonElement description = jsonObject.get("description");
                    if (description != null && description.isJsonPrimitive())
                        this.description = description.getAsString();
                    else throw new InvalidPacketException();

                    JsonElement cardType = jsonObject.get("cardType");
                    if (cardType != null && cardType.isJsonPrimitive())
                        this.type = CardType.valueOf(cardType.getAsString());
                    else throw new InvalidPacketException();

                    JsonElement value = jsonObject.get("value");
                    if (value != null && value.isJsonPrimitive())
                        this.value = value.getAsString();
                    else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public String getDescription() {
        return description;
    }

    public JsonObject toJsonObject() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("name", name);
        out.addProperty("description", description);
        out.addProperty("cardType", type.name());
        out.addProperty("value", value);
        return out;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public CardType getType() {
        return type;
    }

    public enum CardType {
        GET_OUT_OF_JAIL,
        MODIFIED_RENT,
        BALANCE_CHANGE,
        GO_TO_NEAREST_OF_TYPE,
        GO_TO,
        GO_TO_TELEPORT,
        MOVE,
        PAY_PLAYERS
    }
}
