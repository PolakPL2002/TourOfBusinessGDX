package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.RollPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.UUID;

import static pl.greenmc.tob.game.util.Logger.error;

public class RollDialog extends Dialog {

    public RollDialog() {
        super(new Button("Rzuć"), 100, 50);
        Button button = (Button) getChild();
        button.setClickCallback(() -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new RollPacket(), new SentPacket.Callback() {
                    @Override
                    public void success(@NotNull UUID uuid, @Nullable JsonObject response) {

                    }

                    @Override
                    public void failure(@NotNull UUID uuid, @NotNull SentPacket.FailureReason reason) {
                        error("Failed to roll the dice: " + reason);
                    }
                }, false);
            } catch (ConnectionNotAliveException e) {
                error(e);
            }
        });
    }
}
