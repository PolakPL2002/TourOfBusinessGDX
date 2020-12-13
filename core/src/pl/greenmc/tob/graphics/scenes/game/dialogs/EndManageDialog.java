package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
            Color color = Color.BLACK;
            String groupName = "";
            switch (tile.getType()) {
                case START:
                    name = "Start";
                    break;
                case CITY:
                    name = ((Tile.CityTileData) tile.getData()).getName();
                    color = ((Tile.CityTileData) tile.getData()).getTileGroup().getColor();
                    groupName = ((Tile.CityTileData) tile.getData()).getTileGroup().getName();
                    break;
                case STATION:
                    name = ((Tile.StationTileData) tile.getData()).getName();
                    color = ((Tile.StationTileData) tile.getData()).getTileGroup().getColor();
                    groupName = ((Tile.StationTileData) tile.getData()).getTileGroup().getName();
                    break;
                case UTILITY:
                    name = ((Tile.UtilityTileData) tile.getData()).getName();
                    color = ((Tile.UtilityTileData) tile.getData()).getTileGroup().getColor();
                    groupName = ((Tile.UtilityTileData) tile.getData()).getTileGroup().getName();
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
                    color = ((Tile.JailTileData) tile.getData()).getTileGroup().getColor();
                    groupName = ((Tile.JailTileData) tile.getData()).getTileGroup().getName();
                    break;
                case GO_TO_JAIL:
                    name = "Idź do więzienia";
                    color = ((Tile.GoToJailTileData) tile.getData()).getTileGroup().getColor();
                    groupName = ((Tile.GoToJailTileData) tile.getData()).getTileGroup().getName();
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
            VSplitPane property = new VSplitPane();

            //Color bar
            property.addChild(new SolidColor(color), new VSplitPane.ElementOptions(30, VSplitPane.ElementOptions.WidthMode.FIXED));
            HSplitPane content = new HSplitPane();
            property.addChild(new PaddingPane(content, 10), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
            VSplitPane topRow = new VSplitPane(), bottomRow = new VSplitPane();
            topRow.addChild(new HSplitPane()
                            .addChild(new Label(name, 24, false), new HSplitPane.ElementOptions(4, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                            .addChild(new Label(groupName, 18, false), new HSplitPane.ElementOptions(3, HSplitPane.ElementOptions.HeightMode.VARIABLE)),
                    new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
            Button sell = new Button("Sprzedaj");
            sell.applyNoTheme();
            topRow.addChild(sell, new VSplitPane.ElementOptions(100, VSplitPane.ElementOptions.WidthMode.FIXED));

            content.addChild(bottomRow, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
            content.addChild(topRow, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));

            HSplitPane propertyContainer = new HSplitPane().addChild(new SolidColor(Color.BLACK), new HSplitPane.ElementOptions(2, HSplitPane.ElementOptions.HeightMode.FIXED))
                    .addChild(property, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));

            propertiesPane.addChild(propertyContainer, 150);
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
