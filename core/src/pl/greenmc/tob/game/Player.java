package pl.greenmc.tob.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;

public class Player {
    public static String TYPE = "PLAYER";

    private final int ID;
    @NotNull
    private final String identity;
    @NotNull
    private final String name;

    public Player(int ID, @NotNull String identity, @NotNull String name) {
        this.ID = ID;
        this.identity = identity;
        this.name = name;
    }

    public Player(@NotNull JsonObject jsonObject) throws InvalidPacketException {
        if (jsonObject.has("type")) {
            //Check type
            try {
                JsonElement type = jsonObject.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement ID = jsonObject.get("ID");
                    if (ID != null && ID.isJsonPrimitive()) this.ID = ID.getAsInt();
                    else throw new InvalidPacketException();

                    JsonElement identity = jsonObject.get("identity");
                    if (identity != null && identity.isJsonPrimitive()) this.identity = identity.getAsString();
                    else throw new InvalidPacketException();

                    JsonElement name = jsonObject.get("name");
                    if (name != null && name.isJsonPrimitive()) this.name = name.getAsString();
                    else throw new InvalidPacketException();
                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public int getID() {
        return ID;
    }

    @NotNull
    public String getIdentity() {
        return identity;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public JsonObject toJsonObject() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("ID", ID);
        out.addProperty("identity", identity);
        out.addProperty("name", name);
        return out;
    }
}
