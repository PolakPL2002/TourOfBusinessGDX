package pl.greenmc.tob.graphics.scenes.game.dialogs.trade;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.ArrayList;
import java.util.Arrays;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Utilities.makeMoney;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.getTileName;

public class IncomingTradeDialog extends Dialog {
    private final Button accept, reject;
    @NotNull
    private final Map map;
    @NotNull
    private final String player1Name;
    private final ArrayList<TradeDialog.Property> player1Offer = new ArrayList<>(), player2Offer = new ArrayList<>();
    @NotNull
    private final String player2Name;
    private final VSplitPane vSplitPane;

    public IncomingTradeDialog(@NotNull GameState.Trade trade, @NotNull Runnable onAccept, @NotNull Runnable onReject, @NotNull Map map, @NotNull String player1Name, @NotNull String player2Name) {
        super(new HSplitPane(), Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f);
        this.map = map;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Offer.addAll(Arrays.asList(trade.getPlayer1Offer()));
        this.player2Offer.addAll(Arrays.asList(trade.getPlayer2Offer()));

        HSplitPane pane = (HSplitPane) getChild();
        VSplitPane pane1 = new VSplitPane();

        reject = new Button("Odrzuć");
        reject.setClickCallback(onReject);
        reject.setFontSize((int) (TOB.getFontBase() / 6));
        reject.applyNoTheme();

        accept = new Button("Zaakceptuj");
        accept.setClickCallback(onAccept);
        accept.setFontSize((int) (TOB.getFontBase() / 6));
        accept.applyYesTheme();

        pane.addChild(pane1, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.addChild(vSplitPane = new VSplitPane(), new HSplitPane.ElementOptions(526, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.setDrawBackground(true);
        pane.setBackgroundColor(GlobalTheme.backgroundColor);

        pane1.addChild(reject, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        pane1.addChild(accept, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));

        generate();
    }

    private void generate() {
        vSplitPane.clearChildren();

        long player1OfferValue = 0, player2OfferValue = 0;
        for (TradeDialog.Property property : player1Offer) {
            if (property.getType() == TradeDialog.Property.PropertyType.TILE)
                player1OfferValue += property.getMonetaryValue();
            else
                player1OfferValue += property.getValue();
        }
        for (TradeDialog.Property property : player2Offer) {
            if (property.getType() == TradeDialog.Property.PropertyType.TILE)
                player2OfferValue += property.getMonetaryValue();
            else
                player2OfferValue += property.getValue();
        }
        VScrollPane player1Offer = new VScrollPane(),
                player2Offer = new VScrollPane();
        vSplitPane
                .addChild(new HSplitPane()
                                .addChild(new Label("Wartość: " + makeMoney(player1OfferValue), (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(player1Offer, new HSplitPane.ElementOptions(466, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Oferta gracza " + player1Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                .addChild(new HSplitPane()
                                .addChild(new Label("Wartość: " + makeMoney(player2OfferValue), (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(player2Offer, new HSplitPane.ElementOptions(466, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                                .addChild(new Label("Oferta gracza " + player2Name, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                        new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        boolean bc = true;
        //Player 1
        for (TradeDialog.Property property : this.player1Offer)
            addPropertyToList(player1Offer, property, bc = !bc);
        bc = true;
        //Player 2
        for (TradeDialog.Property property : this.player2Offer)
            addPropertyToList(player2Offer, property, bc = !bc);
    }

    private void addPropertyToList(@NotNull VScrollPane scrollPane, @NotNull TradeDialog.Property property, boolean bc) {
        String type = "", name = "";
        switch (property.getType()) {
            case TILE:
                name = getTileName(map.getTiles()[(int) property.getValue()]);
                type = "Posiadłość";
                break;
            case MONEY:
                name = makeMoney(property.getValue());
                type = "Pieniądze";
                break;
        }
        HSplitPane pane;
        scrollPane.addChild(pane = new HSplitPane(), TOB.getFontBase() / 1.2f + (property.getType() == TradeDialog.Property.PropertyType.MONEY ? TOB.getFontBase() : 0));
        pane.setDrawBackground(true);
        pane.setBackgroundColor(bc ? GlobalTheme.scheme.color200() : GlobalTheme.scheme.color300());
        VSplitPane element = new VSplitPane();
        element.addChild(
                new Label(name, (int) (TOB.getFontBase() / 5), false),
                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));

        pane.addChild(element,
                new HSplitPane.ElementOptions(60, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(new Label(type, (int) (TOB.getFontBase() / 6), false, true),
                new HSplitPane.ElementOptions(40, HSplitPane.ElementOptions.HeightMode.VARIABLE));

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() * 0.8f);
        setHeight(Gdx.graphics.getHeight() * 0.8f);
        reject.setFontSize((int) (TOB.getFontBase() / 6));
        accept.setFontSize((int) (TOB.getFontBase() / 6));
        generate();
    }
}
