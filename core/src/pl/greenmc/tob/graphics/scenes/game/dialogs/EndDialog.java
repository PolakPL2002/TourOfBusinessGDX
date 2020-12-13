package pl.greenmc.tob.graphics.scenes.game.dialogs;

import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.EndGameActionPacket;
import pl.greenmc.tob.game.netty.packets.game.EndGameTimeoutResetPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.TransparentColor;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.warning;

public class EndDialog extends Dialog {
    public EndDialog(@NotNull Runnable manageCallback, @NotNull Runnable tradeCallback) {
        super(new HSplitPane(), 300, 165);
        HSplitPane pane = (HSplitPane) getChild();

        Button end = new Button("Zakończ turę");
        Button manage = new Button("Zarządzaj posiadłościami");
        Button trade = new Button("Wymień się");

        end.applyNoTheme();
        end.setClickCallback(() -> TOB.runOnGLThread(() -> {
            onAction();
            try {
                NettyClient.getInstance().getClientHandler().send(
                        new EndGameActionPacket(new EndGameActionPacket.ActionEndTurn()),
                        new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                warning(e);
            }
        }));

        manage.setClickCallback(() -> {
            onAction();
            manageCallback.run();
        });
        trade.setClickCallback(() -> {
            onAction();
            tradeCallback.run();
        });

        pane.addChild(trade, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        pane.addChild(manage, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(15, HSplitPane.ElementOptions.HeightMode.FIXED));
        pane.addChild(end, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
    }

    private void onAction() {
        try {
            NettyClient.getInstance().getClientHandler().send(
                    new EndGameTimeoutResetPacket(),
                    new SentPacket.Callback.BlankCallback(), false);
        } catch (ConnectionNotAliveException e) {
            warning(e);
        }
    }
}
