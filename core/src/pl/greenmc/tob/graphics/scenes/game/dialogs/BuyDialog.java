package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Logger.error;

public class BuyDialog extends Dialog {
    private final Button doBuy, dontBuy;
    private final Label label;

    public BuyDialog(@NotNull Tile tile, @NotNull GameState.GameSettings gameSettings) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.42f);
        String tileName = "<Unknown>";
        long price = -1;
        switch (tile.getType()) {
            case CITY:
                tileName = ((Tile.CityTileData) tile.getData()).getName();
                price = (long) (((Tile.CityTileData) tile.getData()).getValue() * gameSettings.getPriceModifier());
                break;
            case STATION:
                tileName = ((Tile.StationTileData) tile.getData()).getName();
                price = (long) (((Tile.StationTileData) tile.getData()).getValue() * gameSettings.getPriceModifier());
                break;
            case UTILITY:
                tileName = ((Tile.UtilityTileData) tile.getData()).getName();
                price = (long) (((Tile.UtilityTileData) tile.getData()).getValue() * gameSettings.getPriceModifier());
                break;
        }

        ((HSplitPane) getChild())
                .addChild(
                        dontBuy = new Button("Nie"),
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        doBuy = new Button("Tak"),
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        label = new Label("Czy chcesz kupić " + tileName + " za " + Utilities.makeMoney(price) + "?", (int) (TOB.getFontBase() / 5), false),
                        new HSplitPane.ElementOptions(200, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                );

        dontBuy.setClickCallback(makeRunnable(GameState.BuyDecision.DONT_BUY));
        dontBuy.applyNoTheme();
        doBuy.setClickCallback(makeRunnable(GameState.BuyDecision.BUY));
        doBuy.applyYesTheme();
        label.setBackgroundColor(new Color(1, 1, 1, 0.75f));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setHeight(Gdx.graphics.getHeight() * 0.42f);
        setWidth(Gdx.graphics.getWidth() / 2f);
        label.setFontSize((int) (TOB.getFontBase() / 5));
        doBuy.setFontSize((int) (TOB.getFontBase() / 6));
        dontBuy.setFontSize((int) (TOB.getFontBase() / 6));
    }

    @NotNull
    @Contract(pure = true)
    private Runnable makeRunnable(@NotNull final GameState.BuyDecision decision) {
        return () -> {
            try {
                NettyClient.getInstance().getClientHandler().send(new SetBuyDecisionPacket(decision), new SentPacket.Callback.BlankCallback(), false);
            } catch (ConnectionNotAliveException e) {
                error(e);
            }
        };
    }
}
