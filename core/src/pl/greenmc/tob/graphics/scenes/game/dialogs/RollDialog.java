package pl.greenmc.tob.graphics.scenes.game.dialogs;

import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.RollPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.game.util.Logger.error;

public class RollDialog extends Dialog {

    public RollDialog() {
        super(new Button("Rzuć"), 100, 50);
        Button button = (Button) getChild();
        button.setClickCallback(() -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new RollPacket(), new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                error(e);
            }
        });
    }
}
