package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.greenmc.tob.game.GameState;
import pl.greenmc.tob.game.map.Card;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.GameState.getPropertyImprovementCost;
import static pl.greenmc.tob.game.util.Utilities.getTileNumber;
import static pl.greenmc.tob.game.util.Utilities.makeMoney;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.*;

public class PropertyDialog extends Dialog {
    private final Button closeButton;
    @NotNull
    private final GameState.GameSettings gameSettings;
    @NotNull
    private final Map map;
    @NotNull
    private final Tile tile;
    private final Label upgradeCost, group, name, owner, cost;
    private VScrollPane rents;

    public PropertyDialog(@NotNull Map map, @NotNull Tile tile, @NotNull GameState.GameSettings gameSettings, @NotNull Runnable onClose, @Nullable String owner) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.75f);
        this.map = map;
        this.tile = tile;
        this.gameSettings = gameSettings;
        if (tile.getType() != Tile.TileType.STATION &&
                tile.getType() != Tile.TileType.UTILITY &&
                tile.getType() != Tile.TileType.CITY)
            owner = null;
        HSplitPane pane = (HSplitPane) getChild();
        pane.addChild(closeButton = new Button("Zamknij"), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        closeButton.setClickCallback(onClose);
        closeButton.setFontSize((int) (TOB.getFontBase() / 6));
        long cost = 0;

        switch (tile.getType()) {
            case CITY:
                cost = ((Tile.CityTileData) tile.getData()).getValue();
                break;
            case UTILITY:
                cost = ((Tile.UtilityTileData) tile.getData()).getValue();
                break;
            case STATION:
                cost = ((Tile.StationTileData) tile.getData()).getValue();
                break;
        }

        if (tile.getType() == Tile.TileType.CITY)
            pane.addChild(upgradeCost = new Label("Koszt ulepszenia: " + makeMoney(getPropertyImprovementCost(tile, gameSettings)), (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        else
            upgradeCost = null;

        String groupName = getTileGroupName(tile);

        pane.addChild(rents = new VScrollPane(), new HSplitPane.ElementOptions(320 + (upgradeCost == null ? 30 : 0) + (groupName.equals("") ? 30 : 0) + (owner == null ? 30 : 0) + (cost == 0 ? 30 : 0), HSplitPane.ElementOptions.HeightMode.VARIABLE));

        updateRents();

        if (cost > 0)
            pane.addChild(this.cost = new Label("Cena: " + makeMoney(cost), (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        else
            this.cost = null;
        if (owner != null)
            pane.addChild(this.owner = new Label(owner, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        else
            this.owner = null;

        if (!groupName.equals(""))
            pane.addChild(group = new Label(groupName, (int) (TOB.getFontBase() / 8), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        else
            group = null;
        pane.addChild(name = new Label(getTileName(tile), (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        name.setBackgroundColor(getTileGroupColor(tile));
        name.setTextColor(Color.WHITE);
        name.setOutlineColor(Color.BLACK);
        name.setOutlineWidth(1);
        pane.setBackgroundColor(GlobalTheme.backgroundColor);
        pane.setDrawBackground(true);
    }

    private void updateRents() {
        rents.clearChildren();
        switch (tile.getType()) {
            case CITY:
                Tile.CityTileData cityTileData = (Tile.CityTileData) tile.getData();
                for (int i = cityTileData.getMaxLevel(); i >= 0; i--) {
                    rents.addChild(new Label("Poziom " + i + ": " + makeMoney(cityTileData.getRent(i)), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                }
                rents.addChild(new Label("Czynsz", (int) (TOB.getFontBase() / 6), false), TOB.getFontBase() / 2);
                break;
            case UTILITY:
                Tile.UtilityTileData utilityTileData = (Tile.UtilityTileData) tile.getData();
                rents.addChild(new Label("Kwota jest mnożona przez liczbę wyrzuconą na kostce.", (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                for (int i = utilityTileData.getMaxLevel(); i >= 1; i--) {
                    rents.addChild(new Label(i + " posiadane: " + makeMoney(utilityTileData.getMultiplier(i)), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                }
                rents.addChild(new Label("Czynsz", (int) (TOB.getFontBase() / 6), false), TOB.getFontBase() / 2);
                break;
            case STATION:
                Tile.StationTileData stationTileData = (Tile.StationTileData) tile.getData();
                for (int i = stationTileData.getMaxLevel(); i >= 1; i--) {
                    rents.addChild(new Label(i + " posiadane: " + makeMoney(stationTileData.getRent(i)), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                }
                rents.addChild(new Label("Czynsz", (int) (TOB.getFontBase() / 6), false), TOB.getFontBase() / 2);
                break;
            case JAIL:
                Tile.JailTileData jailTileData = (Tile.JailTileData) tile.getData();
                boolean gtjFound = false;
                for (Tile tile : jailTileData.getTileGroup().getTiles()) {
                    if (tile.getType() == Tile.TileType.GO_TO_JAIL) {
                        gtjFound = true;
                        rents.addChild(new Label("Można tu trafić stając na polu " + getTileNumber(map, tile), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                    }
                }
                if (!gtjFound)
                    rents.addChild(new Label("Można tu trafić stając na tym polu.", (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                rents.addChild(new Label("Opłata za wyjście wynosi " + makeMoney(jailTileData.getBailMoney()), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                rents.addChild(new Label("Maksymalna ilośc tur spędzonych w więzieniu wynosi " + jailTileData.getMaxRounds(), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                break;
            case GO_TO_JAIL:
                Tile.GoToJailTileData goToJailTileData = (Tile.GoToJailTileData) tile.getData();
                for (Tile tile : goToJailTileData.getTileGroup().getTiles()) {
                    if (tile.getType() == Tile.TileType.JAIL) {
                        rents.addChild(new Label("To pole prowadzi do więzienia na polu " + getTileNumber(map, tile), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                        break;
                    }
                }
                break;
            case LUXURY_TAX:
                rents.addChild(new Label("Podatek wynosi " + makeMoney(((Tile.LuxuryTaxTileData) tile.getData()).getCost()), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                break;
            case INCOME_TAX:
                rents.addChild(new Label("Podatek wynosi " + makeMoney(((Tile.IncomeTaxTileData) tile.getData()).getCost()), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                break;
            case CHANCE:
                Tile.ChanceTileData chanceTileData = (Tile.ChanceTileData) tile.getData();
                Card[] cards = chanceTileData.getDeck().getCards();
                generateCards(cards);
                break;
            case COMMUNITY_CHEST:
                Tile.CommunityChestTileData communityChestTileData = (Tile.CommunityChestTileData) tile.getData();
                cards = communityChestTileData.getDeck().getCards();
                generateCards(cards);
                break;
            case START:
                rents.addChild(new Label(gameSettings.getStartStandMultiplier() == 1f ? "Kwota nie zmienia się przy stanięciu na polu" : "Kwota jest mnożona " + gameSettings.getStartStandMultiplier() + "x przy stanięciu na polu", (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                rents.addChild(new Label("Kwota za przejście wynosi " + makeMoney(((Tile.StartTileData) tile.getData()).getStartMoney()), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                break;
            case PLACEHOLDER:
                long charge = ((Tile.PlaceholderTileData) tile.getData()).getCharge();
                rents.addChild(new Label(charge == 0 ? "Pole jest bezpłatne" : "Opłata wynosi " + makeMoney(charge), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
                break;
        }
    }

    private void generateCards(@NotNull Card[] cards) {
        for (Card card : cards) {
            switch (card.getType()) {
                case MODIFIED_RENT:
                    rents.addChild(new Label("Modyfikator czynszu: " + (Long.parseLong(card.getValue()) / 10f) + "%", (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case BALANCE_CHANGE:
                    rents.addChild(new Label("Zmiana balansu: " + makeMoney(Long.parseLong(card.getValue())), (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case GO_TO_NEAREST_OF_TYPE:
                    rents.addChild(new Label("Idź do najbliższego pola typu " + card.getValue(), (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case GO_TO:
                    rents.addChild(new Label("Idź na pole " + card.getValue(), (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case GO_TO_TELEPORT:
                    rents.addChild(new Label("Teleportuj się na pole " + card.getValue(), (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case MOVE:
                    rents.addChild(new Label("Przesuń się o " + card.getValue() + " pola", (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
                case PAY_PLAYERS:
                    long l = Long.parseLong(card.getValue());
                    if (l < 0)
                        rents.addChild(new Label("Pobierz " + makeMoney(-Long.parseLong(card.getValue())) + " od wszystkich graczy", (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    else
                        rents.addChild(new Label("Zapłać " + makeMoney(Long.parseLong(card.getValue())) + " wszystkim graczom", (int) (TOB.getFontBase() / 8), false, true), TOB.getFontBase() / 3);
                    break;
            }
            rents.addChild(new Label(card.getDescription(), (int) (TOB.getFontBase() / 8), false), TOB.getFontBase() / 3);
            rents.addChild(new Label(card.getName(), (int) (TOB.getFontBase() / 6), false), TOB.getFontBase() / 2);
            rents.addChild(new SolidColor(Color.BLACK), 1);
        }
        rents.addChild(new Label("Karty możliwe do wylosowania", (int) (TOB.getFontBase() / 6), false), TOB.getFontBase() / 2);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() / 2f);
        setHeight(Gdx.graphics.getHeight() * 0.75f);
        closeButton.setFontSize((int) (TOB.getFontBase() / 6));
        name.setFontSize((int) (TOB.getFontBase() / 6));
        if (upgradeCost != null) upgradeCost.setFontSize((int) (TOB.getFontBase() / 8));
        if (group != null) group.setFontSize((int) (TOB.getFontBase() / 8));
        if (owner != null) owner.setFontSize((int) (TOB.getFontBase() / 8));
        if (cost != null) cost.setFontSize((int) (TOB.getFontBase() / 8));
        updateRents();
    }
}
