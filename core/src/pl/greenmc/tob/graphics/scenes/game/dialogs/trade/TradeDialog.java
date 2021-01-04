package pl.greenmc.tob.graphics.scenes.game.dialogs.trade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.netty.InvalidPacketException;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Utilities.makeMoney;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.getTileName;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.onEndAction;

public class TradeDialog extends Dialog {
    private final Button back, trade;
    @NotNull
    private final Map map;
    @NotNull
    private final String player1Name;
    private final ArrayList<Property> player1Properties = new ArrayList<>(), player2Properties = new ArrayList<>(),
            player1Offer = new ArrayList<>(), player2Offer = new ArrayList<>();
    @NotNull
    private final String player2Name;
    private final VSplitPane vSplitPane;
    private Timer timer;

    public TradeDialog(@NotNull Map map, @NotNull String player1Name, @NotNull String player2Name, @NotNull Property[] player1Properties, @NotNull Property[] player2Properties, @NotNull Runnable onBack, @NotNull TradeCallback onTrade) {
        super(new HSplitPane(), Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f);
        this.map = map;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Properties.addAll(Arrays.asList(player1Properties));
        this.player2Properties.addAll(Arrays.asList(player2Properties));

        HSplitPane pane = (HSplitPane) getChild();
        VSplitPane pane1 = new VSplitPane();

        back = new Button("Wróć");
        back.setClickCallback(onBack);
        back.setFontSize((int) (TOB.getFontBase() / 6));

        trade = new Button("Zatwierdź");
        trade.setClickCallback(() -> {
            if (this.player1Offer.size() > 0 || this.player2Offer.size() > 0)
                onTrade.run(player1Offer.toArray(new Property[0]), player2Offer.toArray(new Property[0]));
        });
        trade.setFontSize((int) (TOB.getFontBase() / 6));

        pane.addChild(pane1, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.addChild(vSplitPane = new VSplitPane(), new HSplitPane.ElementOptions(526, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.setDrawBackground(true);
        pane.setBackgroundColor(GlobalTheme.backgroundColor);

        pane1.addChild(back, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        pane1.addChild(trade, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));

        generate();
    }

    @Override
    public void setup() {
        super.setup();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onEndAction();
            }
        }, 0, 3000);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (timer != null) timer.cancel();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() * 0.8f);
        setHeight(Gdx.graphics.getHeight() * 0.8f);
        back.setFontSize((int) (TOB.getFontBase() / 6));
        trade.setFontSize((int) (TOB.getFontBase() / 6));
        generate();
    }

    private void generate() {
        vSplitPane.clearChildren();

        long player1OfferValue = 0, player2OfferValue = 0;
        for (Property property : player1Offer) {
            if (property.type == Property.PropertyType.TILE)
                player1OfferValue += property.monetaryValue;
            else
                player1OfferValue += property.value;
        }
        for (Property property : player2Offer) {
            if (property.type == Property.PropertyType.TILE)
                player2OfferValue += property.monetaryValue;
            else
                player2OfferValue += property.value;
        }
        VScrollPane player1 = new VScrollPane(),
                player1Offer = new VScrollPane(),
                player2Offer = new VScrollPane(),
                player2 = new VScrollPane();
        vSplitPane
                .addChild(new HSplitPane()
                                .addChild(player1, new HSplitPane.ElementOptions(496, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Posiadłości gracza " + player1Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                .addChild(new HSplitPane()
                                .addChild(new Label("Wartość: " + makeMoney(player1OfferValue), (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(player1Offer, new HSplitPane.ElementOptions(466, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Oferta gracza " + player1Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                .addChild(new HSplitPane()
                                .addChild(new Label("Wartość: " + makeMoney(player2OfferValue), (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(player2Offer, new HSplitPane.ElementOptions(466, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Oferta gracza " + player2Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                .addChild(new HSplitPane()
                                .addChild(player2, new HSplitPane.ElementOptions(496, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Posiadłości gracza " + player2Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        boolean bc = true;
        //Player 1
        for (Property property : player1Properties)
            addPropertyToList(player1, property, 1, Direction.PLAYER1_TO_OFFER, bc = !bc);
        bc = true;
        for (Property property : this.player1Offer)
            addPropertyToList(player1Offer, property, 2, Direction.PLAYER1_FROM_OFFER, bc = !bc);
        bc = true;
        //Player 2
        for (Property property : player2Properties)
            addPropertyToList(player2, property, 2, Direction.PLAYER2_TO_OFFER, bc = !bc);
        bc = true;
        for (Property property : this.player2Offer)
            addPropertyToList(player2Offer, property, 1, Direction.PLAYER2_FROM_OFFER, bc = !bc);
        if (this.player1Offer.size() > 0 || this.player2Offer.size() > 0)
            trade.applyYesTheme();
        else
            trade.applyDisabledTheme();
    }

    private void addPropertyToList(@NotNull VScrollPane scrollPane, @NotNull Property property, int direction, @NotNull Direction dir, boolean bc) {
        String type = "", name = "";
        switch (property.type) {
            case TILE:
                name = getTileName(map.getTiles()[(int) property.value]);
                type = "Posiadłość";
                break;
            case MONEY:
                name = makeMoney(property.value);
                type = "Pieniądze";
                break;
        }
        HSplitPane pane;
        scrollPane.addChild(pane = new HSplitPane(), TOB.getFontBase() / 1.2f + (property.type == Property.PropertyType.MONEY ? TOB.getFontBase() : 0));
        pane.setDrawBackground(true);
        pane.setBackgroundColor(bc ? GlobalTheme.scheme.color200() : GlobalTheme.scheme.color300());
        Button button = new Button(direction == 1 ? "->" : "<-");
        VSplitPane element = new VSplitPane();
        button.setFontSize((int) (TOB.getFontBase() / 5));
        if (direction != 1)
            element.addChild(button, new VSplitPane.ElementOptions(TOB.getFontBase() / 2, VSplitPane.ElementOptions.WidthMode.FIXED));
        element.addChild(
                new Label(name, (int) (TOB.getFontBase() / 5), false),
                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        if (direction == 1)
            element.addChild(button, new VSplitPane.ElementOptions(TOB.getFontBase() / 2, VSplitPane.ElementOptions.WidthMode.FIXED));
        if (property.type == Property.PropertyType.MONEY)
            pane.addChild(new Label("CTRL+SHIFT+ALT: 1$\n" +
                            "CTRL+ALT: 10$\n" +
                            "CTRL+SHIFT: 100$\n" +
                            "CTRL: 1.000$\n" +
                            "10.000$\n" +
                            "SHIFT: 100.000$\n" +
                            "ALT: 1.000.000$\n" +
                            "SHIFT+ALT: 10.000.000$", (int) (TOB.getFontBase() / 12), false, true),
                    new HSplitPane.ElementOptions(120, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(element,
                new HSplitPane.ElementOptions(60, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(new Label(type, (int) (TOB.getFontBase() / 6), false, true),
                new HSplitPane.ElementOptions(40, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        button.setClickCallback(() -> {
            if (property.type == Property.PropertyType.TILE)
                switch (dir) {
                    case PLAYER1_TO_OFFER:
                        if (player1Properties.remove(property))
                            player1Offer.add(property);
                        for (long id : property.group) {
                            Property found = null;
                            for (Property property1 : player1Properties)
                                if (property1.type == Property.PropertyType.TILE && property1.value == id) {
                                    found = property1;
                                    break;
                                }
                            if (found != null) {
                                if (player1Properties.remove(found))
                                    player1Offer.add(found);
                            }
                        }
                        break;
                    case PLAYER1_FROM_OFFER:
                        if (player1Offer.remove(property))
                            player1Properties.add(property);
                        for (long id : property.group) {
                            Property found = null;
                            for (Property property1 : player1Offer)
                                if (property1.type == Property.PropertyType.TILE && property1.value == id) {
                                    found = property1;
                                    break;
                                }
                            if (found != null) {
                                if (player1Offer.remove(found))
                                    player1Properties.add(found);
                            }
                        }
                        break;
                    case PLAYER2_TO_OFFER:
                        if (player2Properties.remove(property))
                            player2Offer.add(property);
                        for (long id : property.group) {
                            Property found = null;
                            for (Property property1 : player2Properties)
                                if (property1.type == Property.PropertyType.TILE && property1.value == id) {
                                    found = property1;
                                    break;
                                }
                            if (found != null) {
                                if (player2Properties.remove(found))
                                    player2Offer.add(found);
                            }
                        }
                        break;
                    case PLAYER2_FROM_OFFER:
                        if (player2Offer.remove(property))
                            player2Properties.add(property);
                        for (long id : property.group) {
                            Property found = null;
                            for (Property property1 : player2Offer)
                                if (property1.type == Property.PropertyType.TILE && property1.value == id) {
                                    found = property1;
                                    break;
                                }
                            if (found != null) {
                                if (player2Offer.remove(found))
                                    player2Properties.add(found);
                            }
                        }
                        break;
                }
            else
                switch (dir) {
                    case PLAYER1_TO_OFFER:
                        transferMoney(property, player1Properties, player1Offer);
                        break;
                    case PLAYER1_FROM_OFFER:
                        transferMoney(property, player1Offer, player1Properties);
                        break;
                    case PLAYER2_TO_OFFER:
                        transferMoney(property, player2Properties, player2Offer);
                        break;
                    case PLAYER2_FROM_OFFER:
                        transferMoney(property, player2Offer, player2Properties);
                        break;
                }
            TOB.runOnGLThread(this::generate);
        });
    }

    private void transferMoney(@NotNull Property property, @NotNull ArrayList<Property> player1Properties, @NotNull ArrayList<Property> player1Offer) {
        long targetAmount;
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT),
                shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT),
                ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        if (alt && ctrl && shift)
            targetAmount = 1;
        else if (ctrl && alt)
            targetAmount = 10;
        else if (ctrl && shift)
            targetAmount = 100;
        else if (alt && shift)
            targetAmount = 10000000;
        else if (ctrl)
            targetAmount = 1000;
        else if (shift)
            targetAmount = 100000;
        else if (alt)
            targetAmount = 1000000;
        else
            targetAmount = 10000;

        if (player1Properties.contains(property)) {
            Property targetProperty = null;
            long amount = Math.min(targetAmount, property.value);
            for (Property property1 : player1Offer) {
                if (property1.type == Property.PropertyType.MONEY) {
                    targetProperty = property1;
                    break;
                }
            }
            if (targetProperty == null) {
                targetProperty = new Property(Property.PropertyType.MONEY, amount, new long[0], 0);
                player1Offer.add(targetProperty);
            } else {
                targetProperty.value += amount;
            }
            property.value -= amount;
            if (property.value <= 0)
                player1Properties.remove(property);
        }
    }

    enum Direction {
        PLAYER1_TO_OFFER,
        PLAYER1_FROM_OFFER,
        PLAYER2_TO_OFFER,
        PLAYER2_FROM_OFFER
    }

    public interface TradeCallback {
        void run(Property[] player1Offer, Property[] player2Offer);
    }

    public static class Property {
        public static final String TYPE = "PROPERTY";
        private final long[] group;
        private final long monetaryValue;
        @NotNull
        private final PropertyType type;
        private long value;

        public Property(@NotNull PropertyType type, long value, long[] group, long monetaryValue) {
            this.type = type;
            this.value = value;
            this.group = group;
            this.monetaryValue = monetaryValue;
        }

        public Property(@NotNull JsonObject jsonObject) throws InvalidPacketException {
            if (jsonObject.has("type")) {
                //Check type
                try {
                    JsonElement type = jsonObject.get("type");
                    if (type != null && type.isJsonPrimitive() && type.getAsString().equalsIgnoreCase(TYPE)) {
                        //Decode values
                        JsonElement monetaryValue = jsonObject.get("monetaryValue");
                        if (monetaryValue != null && monetaryValue.isJsonPrimitive())
                            this.monetaryValue = monetaryValue.getAsLong();
                        else throw new InvalidPacketException();

                        JsonElement value = jsonObject.get("value");
                        if (value != null && value.isJsonPrimitive()) this.value = value.getAsInt();
                        else throw new InvalidPacketException();

                        JsonElement pType = jsonObject.get("pType");
                        if (pType != null && pType.isJsonPrimitive())
                            this.type = PropertyType.valueOf(pType.getAsString());
                        else throw new InvalidPacketException();

                        JsonElement group = jsonObject.get("group");
                        if (group != null && group.isJsonArray()) {
                            JsonArray array = group.getAsJsonArray();
                            this.group = new long[array.size()];
                            int i = 0;
                            for (JsonElement g : array)
                                if (g != null && g.isJsonPrimitive()) {
                                    this.group[i] = g.getAsLong();
                                    i++;
                                } else throw new InvalidPacketException();
                        } else throw new InvalidPacketException();
                    } else throw new InvalidPacketException();
                } catch (ClassCastException ignored) {
                    throw new InvalidPacketException();
                }
            } else throw new InvalidPacketException();
        }

        public long getMonetaryValue() {
            return monetaryValue;
        }

        @NotNull
        public JsonObject toJsonObject() {
            JsonObject out = new JsonObject();
            out.addProperty("type", TYPE);
            out.addProperty("value", value);
            out.addProperty("monetaryValue", monetaryValue);
            out.addProperty("pType", type.name());
            JsonArray group = new JsonArray();
            for (long g : this.group) {
                group.add(g);
            }
            out.add("group", group);
            return out;
        }

        @NotNull
        public PropertyType getType() {
            return type;
        }

        public long getValue() {
            return value;
        }

        public enum PropertyType {
            MONEY,
            TILE
        }
    }
}
