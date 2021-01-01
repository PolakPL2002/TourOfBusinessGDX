package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.SetJailDecisionPacket;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.TransparentColor;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.error;

public class JailDialog extends Dialog {
    private final Button roll, pay, card;

    public JailDialog(long bailAmount, boolean showCardButton, boolean showBailButton) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 5f);
        HSplitPane pane = (HSplitPane) getChild();
        if (showCardButton) {
            pane.addChild(card = new Button("Użyj karty"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
            card.setClickCallback(makeRunnable(GameState.JailDecision.CARD));
        } else {
            card = null;
            pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        }
        if (showBailButton) {
            pane.addChild(pay = new Button("Zapłać " + Utilities.makeMoney(bailAmount)), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
            pay.setClickCallback(makeRunnable(GameState.JailDecision.PAY));
        } else {
            pay = null;
            pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        }
        pane.addChild(roll = new Button("Wyrzuć tą samą liczbę na wszystkich kościach"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        roll.setClickCallback(makeRunnable(GameState.JailDecision.ROLL));
        roll.setFontSize((int) (TOB.getFontBase() / 6));
        if (pay != null) pay.setFontSize((int) (TOB.getFontBase() / 6));
        if (card != null) card.setFontSize((int) (TOB.getFontBase() / 6));
    }

    @NotNull
    @Contract(pure = true)
    private Runnable makeRunnable(@NotNull final GameState.JailDecision decision) {
        return () -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new SetJailDecisionPacket(decision), new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                error(e);
            }
        };
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() / 4f);
        setHeight(Gdx.graphics.getHeight() / 5f);
        roll.setFontSize((int) (TOB.getFontBase() / 6));
        if (pay != null) pay.setFontSize((int) (TOB.getFontBase() / 6));
        if (card != null) card.setFontSize((int) (TOB.getFontBase() / 6));
    }
}
