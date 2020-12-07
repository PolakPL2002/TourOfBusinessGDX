package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.SetBuyDecisionPacket;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.Label;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.UUID;

import static pl.greenmc.tob.game.util.Logger.error;

public class BuyDialog extends Dialog {
    public BuyDialog(@NotNull Tile tile) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 2f, 300);
        String tileName = "<Unknown>";
        long price = -1;
        switch (tile.getType()) {
            case CITY:
                tileName = ((Tile.CityTileData) tile.getData()).getName();
                price = ((Tile.CityTileData) tile.getData()).getValue();
                break;
            case STATION:
                tileName = ((Tile.StationTileData) tile.getData()).getName();
                price = ((Tile.StationTileData) tile.getData()).getValue();
                break;
            case UTILITY:
                tileName = ((Tile.UtilityTileData) tile.getData()).getName();
                price = ((Tile.UtilityTileData) tile.getData()).getValue();
                break;
        }
        Button doBuy, dontBuy;
        Label label;
        ((HSplitPane) getChild()).addChild(dontBuy = new Button("Nie"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(doBuy = new Button("Tak"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(label = new Label("Czy chcesz kupić " + tileName + " za " + Utilities.makeMoney(price) + "?", 24, false), new HSplitPane.ElementOptions(200, HSplitPane.ElementOptions.HeightMode.FIXED));

        dontBuy.setClickCallback(makeRunnable(GameState.BuyDecision.DONT_BUY));
        dontBuy.applyNoTheme();
        doBuy.setClickCallback(makeRunnable(GameState.BuyDecision.BUY));
        doBuy.applyYesTheme();
        label.setBackgroundColor(new Color(1, 1, 1, 0.5f));
    }

    @NotNull
    @Contract(pure = true)
    private Runnable makeRunnable(@NotNull final GameState.BuyDecision decision) {
        return () -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new SetBuyDecisionPacket(decision), new SentPacket.Callback() {
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
