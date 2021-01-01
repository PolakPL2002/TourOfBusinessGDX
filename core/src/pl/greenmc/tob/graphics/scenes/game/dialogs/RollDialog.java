package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.RollPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.error;

public class RollDialog extends Dialog {

    public RollDialog() {
        super(new Button("Rzuć"), Gdx.graphics.getWidth() / 12f, Gdx.graphics.getHeight() / 15f);
        Button button = (Button) getChild();
        button.setFontSize((int) (TOB.getFontBase() / 6));
        button.setClickCallback(() -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new RollPacket(), new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                error(e);
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() / 12f);
        setHeight(Gdx.graphics.getHeight() / 12f);
        ((Button) getChild()).setFontSize((int) (TOB.getFontBase() / 6));
    }
}
