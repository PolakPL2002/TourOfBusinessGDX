package pl.greenmc.tob.graphics.scenes.game.dialogs;

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
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.game.util.Logger.error;

public class JailDialog extends Dialog {
    public JailDialog(long bailAmount, boolean showCardButton, boolean showBailButton) {
        super(new HSplitPane(), 100, 150);
        Button roll, pay, card;
        HSplitPane pane = (HSplitPane) getChild();
        if (showCardButton) {
            pane.addChild(card = new Button("Użyj karty"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
            card.setClickCallback(makeRunnable(GameState.JailDecision.CARD));
        }
        if (showBailButton) {
            pane.addChild(pay = new Button("Zapłać " + Utilities.makeMoney(bailAmount)), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
            pay.setClickCallback(makeRunnable(GameState.JailDecision.PAY));
        }
        pane.addChild(roll = new Button("Wyrzuć tą samą liczbę na wszystkich kościach"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        roll.setClickCallback(makeRunnable(GameState.JailDecision.ROLL));
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
}
