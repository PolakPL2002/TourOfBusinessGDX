package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.EndGameActionPacket;
import pl.greenmc.tob.game.netty.packets.game.EndGameTimeoutResetPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.TransparentColor;
import pl.greenmc.tob.graphics.elements.VSplitPane;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.warning;

public class EndDialog extends Dialog {
    public EndDialog(@NotNull Runnable manageCallback, @NotNull Runnable tradeCallback) {
        super(new HSplitPane(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HSplitPane pane = (HSplitPane) getChild();
        HSplitPane innerPane = new HSplitPane();
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                .addChild(new VSplitPane()
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                        .addChild(innerPane, new VSplitPane.ElementOptions(200, VSplitPane.ElementOptions.WidthMode.FIXED))
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)), new HSplitPane.ElementOptions(165, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));


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

        innerPane.addChild(trade, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        innerPane.addChild(manage, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        innerPane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(15, HSplitPane.ElementOptions.HeightMode.FIXED));
        innerPane.addChild(end, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
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
