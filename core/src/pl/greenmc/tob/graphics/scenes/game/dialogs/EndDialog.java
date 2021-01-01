package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.EndGameActionPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.TransparentColor;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.warning;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.onEndAction;

public class EndDialog extends Dialog {
    private final Button end, end2, manage, trade;

    public EndDialog(@NotNull Runnable manageCallback, @NotNull Runnable tradeCallback) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 3f);
        HSplitPane pane = (HSplitPane) getChild();

        end = new Button("Zakończ turę");
        end2 = new Button("Zakończ turę");
        manage = new Button("Zarządzaj posiadłościami");
        trade = new Button("Wymień się");

        end.applyNoTheme();
        end.setClickCallback(() -> TOB.runOnGLThread(() -> {
            onEndAction();
            try {
                NettyClient.getInstance().getClientHandler().send(
                        new EndGameActionPacket(new EndGameActionPacket.ActionEndTurn()),
                        new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                warning(e);
            }
        }));

        end2.applyNoTheme();
        end2.setClickCallback(() -> TOB.runOnGLThread(() -> {
            onEndAction();
            try {
                NettyClient.getInstance().getClientHandler().send(
                        new EndGameActionPacket(new EndGameActionPacket.ActionEndTurn()),
                        new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                warning(e);
            }
        }));

        manage.setClickCallback(() -> {
            onEndAction();
            manageCallback.run();
        });
        trade.setClickCallback(() -> {
            onEndAction();
            tradeCallback.run();
        });
        pane.addChild(end2, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(15, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(trade, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(manage, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(15, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(end, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        end.setFontSize((int) (TOB.getFontBase() / 6));
        end2.setFontSize((int) (TOB.getFontBase() / 6));
        manage.setFontSize((int) (TOB.getFontBase() / 6));
        trade.setFontSize((int) (TOB.getFontBase() / 6));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() / 4f);
        setHeight(Gdx.graphics.getHeight() * 0.23f);
        end.setFontSize((int) (TOB.getFontBase() / 6));
        end2.setFontSize((int) (TOB.getFontBase() / 6));
        manage.setFontSize((int) (TOB.getFontBase() / 6));
        trade.setFontSize((int) (TOB.getFontBase() / 6));
    }
}
