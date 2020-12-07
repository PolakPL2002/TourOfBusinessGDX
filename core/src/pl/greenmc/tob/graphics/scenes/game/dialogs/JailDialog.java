package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.SetJailDecisionPacket;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.UUID;

import static pl.greenmc.tob.game.util.Logger.error;

public class JailDialog extends Dialog {
    public JailDialog() {
        super(new HSplitPane(), 100, 150);
        Button roll, pay, card;
        ((HSplitPane) getChild()).addChild(card = new Button("Użyj karty"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(pay = new Button("Zapłać <tutaj kwota>"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(roll = new Button("Wyrzuć tą samą liczbę na wszystkich kościach"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        roll.setClickCallback(makeRunnable(GameState.JailDecision.ROLL));
        pay.setClickCallback(makeRunnable(GameState.JailDecision.PAY));
        card.setClickCallback(makeRunnable(GameState.JailDecision.CARD));
    }

    @NotNull
    @Contract(pure = true)
    private Runnable makeRunnable(@NotNull final GameState.JailDecision decision) {
        return () -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new SetJailDecisionPacket(decision), new SentPacket.Callback() {
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
        };
    }
}
