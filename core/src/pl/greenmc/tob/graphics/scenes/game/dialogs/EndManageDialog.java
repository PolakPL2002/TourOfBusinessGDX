package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.netty.ConnectionNotAliveException;
import pl.greenmc.tob.game.netty.SentPacket;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.game.netty.packets.game.EndGameTimeoutResetPacket;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.game.util.Logger.warning;

public class EndManageDialog extends Dialog {

    private Timer timer;

    public EndManageDialog(@NotNull Runnable onBack, Tile.TileGroup[] tileGroups, Tile[] playerTiles) {
        super(new HSplitPane(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HSplitPane pane = (HSplitPane) getChild();
        HSplitPane innerPane = new HSplitPane();
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                .addChild(new VSplitPane()
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                        .addChild(innerPane, new VSplitPane.ElementOptions(6, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)), new HSplitPane.ElementOptions(6, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                .addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        VScrollPane groupsPane = new VScrollPane(),
                propertiesPane = new VScrollPane();
        Button backButton = new Button("Wróć");
        backButton.setClickCallback(onBack);
        innerPane.addChild(backButton, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(new VSplitPane()
                                .addChild(groupsPane, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                                .addChild(propertiesPane, new VSplitPane.ElementOptions(5, VSplitPane.ElementOptions.WidthMode.VARIABLE)),
                        new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        for (Tile.TileGroup tileGroup : tileGroups) {
            groupsPane.addChild(new Button(tileGroup.getName()), 30);
        }
        Button groupAllButton = new Button("<Wszystkie>");
        groupAllButton.applyYesTheme();
        groupsPane.addChild(groupAllButton, 30);
        for (Tile tile : playerTiles) {
            String name = "";
            switch (tile.getType()) {
                case START:
                    name = "Start";
                    break;
                case CITY:
                    name = ((Tile.CityTileData) tile.getData()).getName();
                    break;
                case STATION:
                    name = ((Tile.StationTileData) tile.getData()).getName();
                    break;
                case UTILITY:
                    name = ((Tile.UtilityTileData) tile.getData()).getName();
                    break;
                case CHANCE:
                    name = "Szansa";
                    break;
                case COMMUNITY_CHEST:
                    name = "Kasa społeczna";
                    break;
                case PLACEHOLDER:
                    name = "Nic ciekawego";
                    break;
                case JAIL:
                    name = "Więzienie";
                    break;
                case GO_TO_JAIL:
                    name = "Idź do więzienia";
                    break;
                case CHAMPIONSHIPS:
                    name = "Mistrzostwa";
                    break;
                case TRAVEL:
                    name = "Podróż";
                    break;
                case INCOME_TAX:
                    name = "Podatek dochodowy";
                    break;
                case LUXURY_TAX:
                    name = "Podatek luksusowy";
                    break;
            }
            propertiesPane.addChild(new Button(name), 100);
        }
    }

    @Override
    public void setup() {
        super.setup();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    NettyClient.getInstance().getClientHandler().send(
                            new EndGameTimeoutResetPacket(),
                            new SentPacket.Callback.BlankCallback(), false);
                } catch (ConnectionNotAliveException e) {
                    warning(e);
                }
            }
        }, 0, 3000);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (timer != null) timer.cancel();
    }
}
