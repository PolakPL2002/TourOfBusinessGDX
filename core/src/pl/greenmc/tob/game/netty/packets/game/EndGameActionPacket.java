package pl.greenmc.tob.game.netty.packets.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.game.netty.packets.Packet;

public class EndGameActionPacket extends Packet {
    /**
     * Packet data type identifier
     */
    public static String TYPE = "GAME_END_GAME_ACTION";
    private final EndGameAction action;

    /**
     * Default constructor
     */
    public EndGameActionPacket(EndGameAction action) {
        this.action = action;
    }

    /**
     * Decodes packet after network transmission
     *
     * @param objectToDecode Object representing packet to be decoded
     * @throws InvalidPacketException Thrown on decoding error
     */
    public EndGameActionPacket(JsonObject objectToDecode) throws InvalidPacketException {
        super(objectToDecode);
        if (objectToDecode.has("type")) {
            //Check type
            try {
                JsonElement type = objectToDecode.get("type");
                if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                    throw new InvalidPacketException();
                }

                JsonElement action = objectToDecode.get("action");
                if (action != null && action.isJsonObject())
                    this.action = EndGameAction.decode(action.getAsJsonObject());
                else throw new InvalidPacketException();

            } catch (ClassCastException ignored) {
                throw new InvalidPacketException();
            }
        } else throw new InvalidPacketException();
    }

    public EndGameAction getAction() {
        return action;
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
        out.add("action", action.toJsonObject());
        return out;
    }

    public static class ActionEndTurn extends EndGameAction {
        public static final String TYPE = "ACTION_END_TURN";

        public ActionEndTurn() {
        }

        public ActionEndTurn(@NotNull JsonObject objectToDecode) throws InvalidPacketException {
            if (objectToDecode.has("type")) {
                //Check type
                try {
                    JsonElement type = objectToDecode.get("type");
                    if (!(type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE))) {
                        throw new InvalidPacketException();
                    }
                } catch (ClassCastException ignored) {
                    throw new InvalidPacketException();
                }
            } else throw new InvalidPacketException();
        }

        @NotNull
        @Override
        public JsonObject toJsonObject() {
            JsonObject out = new JsonObject();
            out.addProperty("type", TYPE);
            return out;
        }
    }

    public static abstract class EndGameAction {
        @NotNull
        public abstract JsonObject toJsonObject();

        @NotNull
        @Contract("_ -> new")
        public static EndGameAction decode(@NotNull JsonObject objectToDecode) throws InvalidPacketException {
            if (objectToDecode.has("type")) {
                //Check type
                try {
                    JsonElement type = objectToDecode.get("type");
                    if ((type != null && type.isJsonPrimitive())) {
                        switch (type.getAsString()) {
                            case ActionEndTurn.TYPE:
                                return new ActionEndTurn(objectToDecode);
                            default:
                                throw new InvalidPacketException();
                        }
                    } else
                        throw new InvalidPacketException();
                } catch (ClassCastException ignored) {
                    throw new InvalidPacketException();
                }
            } else throw new InvalidPacketException();
        }
    }
}
