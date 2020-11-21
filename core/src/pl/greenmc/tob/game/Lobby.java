package pl.greenmc.tob.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;

//TODO Owner, players, state, gamestate
public class Lobby {
    public static String TYPE = "LOBBY";
    private final int ID;

    public Lobby(int ID) {
        this.ID = ID;
    }

    public Lobby(@NotNull JsonObject jsonObject) throws InvalidPacketException {
        if (jsonObject.has("type")) {
            //Check type
            try {
                JsonElement type = jsonObject.get("type");
                if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                    //Decode values
                    JsonElement ID = jsonObject.get("ID");
                    if (ID != null && ID.isJsonPrimitive()) this.ID = ID.getAsInt();
                    else throw new InvalidPacketException();

                } else throw new InvalidPacketException();
            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public JsonObject toJsonObject() {
        JsonObject out = new JsonObject();
        out.addProperty("type", TYPE);
        out.addProperty("ID", ID);
        return out;
    }

}
