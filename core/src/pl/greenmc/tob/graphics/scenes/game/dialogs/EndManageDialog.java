package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.graphics.scenes.game.GameScene.*;

public class EndManageDialog extends Dialog {
    private final static boolean REQUIRE_ALL_TILES_IN_GROUP_TO_UPGRADE = true;
    @NotNull
    private final Map map;
    @NotNull
    private final LevelCallback onLevel;
    @NotNull
    private final SellCallback onSell;
    @NotNull
    private final Tile[] playerTiles;
    private final VScrollPane propertiesPane;
    private final HashMap<Tile.TileGroup, Button> tileGroupButtons = new HashMap<>();
    @NotNull
    private final Tile.TileGroup[] tileGroups;
    @NotNull
    private final int[] tileLevels;
    private Timer timer;

    public EndManageDialog(@NotNull Runnable onBack, @NotNull SellCallback onSell, @NotNull LevelCallback onLevel, @NotNull Map map, @NotNull Tile.TileGroup[] tileGroups, @NotNull Tile[] playerTiles, @NotNull int[] tileLevels) {
        super(new HSplitPane(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.onSell = onSell;
        this.onLevel = onLevel;
        this.map = map;
        this.tileGroups = tileGroups;
        this.playerTiles = playerTiles;
        this.tileLevels = tileLevels;
        HSplitPane pane = (HSplitPane) getChild();
        HSplitPane innerPane = new HSplitPane();
        pane.addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                .addChild(new VSplitPane()
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                        .addChild(innerPane, new VSplitPane.ElementOptions(6, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                        .addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)), new HSplitPane.ElementOptions(6, HSplitPane.ElementOptions.HeightMode.VARIABLE))
                .addChild(new TransparentColor(), new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        VScrollPane groupsPane = new VScrollPane();
        Button backButton = new Button("Wróć");
        backButton.setClickCallback(onBack);
        innerPane.addChild(backButton, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(new VSplitPane()
                                .addChild(groupsPane, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                                .addChild(propertiesPane = new VScrollPane(), new VSplitPane.ElementOptions(5, VSplitPane.ElementOptions.WidthMode.VARIABLE)),
                        new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        for (Tile.TileGroup tileGroup : tileGroups) {
            Button button = new Button(tileGroup.getName());
            button.setClickCallback(() -> updateProperties(tileGroup));
            groupsPane.addChild(button, 30);
            tileGroupButtons.put(tileGroup, button);
        }
        Button groupAllButton = new Button("<Wszystkie>");
        groupAllButton.applyYesTheme();
        groupAllButton.setClickCallback(() -> updateProperties(null));
        groupsPane.addChild(groupAllButton, 30);
        tileGroupButtons.put(null, groupAllButton);
        updateProperties(null);
    }

    private void updateProperties(@Nullable Tile.TileGroup tileGroup) {
        propertiesPane.clearChildren();
        for (Tile tile : playerTiles) {
            Tile.TileGroup tg = null;
            switch (tile.getType()) {
                case STATION:
                    tg = ((Tile.StationTileData) tile.getData()).getTileGroup();
                    break;
                case UTILITY:
                    tg = ((Tile.UtilityTileData) tile.getData()).getTileGroup();
                    break;
                case CITY:
                    tg = ((Tile.CityTileData) tile.getData()).getTileGroup();
                    break;
                case JAIL:
                    tg = ((Tile.JailTileData) tile.getData()).getTileGroup();
                    break;
                case GO_TO_JAIL:
                    tg = ((Tile.GoToJailTileData) tile.getData()).getTileGroup();
                    break;
            }

            if (tileGroup != null && tg != tileGroup) continue;

            String name = getTileName(tile);
            Color color = getTileGroupColor(tile);
            String groupName = getTileGroupName(tile);
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

            sell.setClickCallback(() -> onSell.run(tile));

            if (tile.getType() == Tile.TileType.CITY) {
                boolean ok;
                if (REQUIRE_ALL_TILES_IN_GROUP_TO_UPGRADE) {
                    ok = true;
                    for (Tile tile1 : ((Tile.CityTileData) tile.getData()).getTileGroup().getTiles()) {
                        int tileID = Utilities.getTileNumber(map, tile1);
                        ok = false;
                        for (Tile tile2 : playerTiles) {
                            if (Objects.equals(tileID, Utilities.getTileNumber(map, tile2))) {
                                ok = true;
                                break;
                            }
                        }
                        if (!ok) break;
                    }
                } else
                    ok = true;
                if (ok) {
                    Button lvlDown = new Button("-"), lvlUp = new Button("+");
                    int tileLevel = tileLevels[Utilities.getTileNumber(map, tile)];
                    Label lvl = new Label(String.valueOf(tileLevel), 24, false);
                    if (tileLevel > 0) {
                        topRow.addChild(lvlDown, new VSplitPane.ElementOptions(50, VSplitPane.ElementOptions.WidthMode.FIXED));
                    }
                    topRow.addChild(lvl, new VSplitPane.ElementOptions(50, VSplitPane.ElementOptions.WidthMode.FIXED));
                    if (tileLevel < ((Tile.CityTileData) tile.getData()).getMaxLevel()) {
                        topRow.addChild(lvlUp, new VSplitPane.ElementOptions(50, VSplitPane.ElementOptions.WidthMode.FIXED));
                    }
                    lvlDown.setClickCallback(() -> onLevel.run(tile, false));
                    lvlUp.setClickCallback(() -> onLevel.run(tile, true));
                }
            }

            topRow.addChild(sell, new VSplitPane.ElementOptions(100, VSplitPane.ElementOptions.WidthMode.FIXED));

            content.addChild(bottomRow, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
            content.addChild(topRow, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));

            HSplitPane propertyContainer = new HSplitPane().addChild(new SolidColor(Color.BLACK), new HSplitPane.ElementOptions(2, HSplitPane.ElementOptions.HeightMode.FIXED))
                    .addChild(property, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));

            propertiesPane.addChild(propertyContainer, 150);
        }
        tileGroupButtons.values().forEach(Button::applyDefaultTheme);
        tileGroupButtons.get(tileGroup).applyYesTheme();
    }

    @Override
    public void setup() {
        super.setup();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onEndAction();
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

    public interface LevelCallback {
        void run(@NotNull Tile tile, boolean isUpgrade);
    }

    public interface SellCallback {
        void run(@NotNull Tile tile);
    }
}