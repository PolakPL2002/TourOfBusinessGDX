package pl.greenmc.tob.game.map;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class SmallerMap extends Map {
    public SmallerMap() {
        super(TOB.getGame().getAssetManager().get("textures/maps/default/board.png"));

        final Tile.TileGroup group1 = new Tile.TileGroup();
        final Tile.TileGroup group2 = new Tile.TileGroup();
        final Tile.TileGroup group3 = new Tile.TileGroup();
        final Tile.TileGroup group4 = new Tile.TileGroup();
        final Tile.TileGroup jailGroup = new Tile.TileGroup();
        final Tile.TileGroup stationGroup = new Tile.TileGroup();
        final Tile tile1 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/start.png"),
                new Tile.StartTileData(300000));
        final Tile tile10 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group2.png"),
                new Tile.CityTileData("Miasto 6", group2, 20000, 100000, 4000));
        final Tile tile11 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/jail.png"),
                new Tile.JailTileData(jailGroup, 200000, 3));
        final Tile tile12 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group3.png"),
                new Tile.CityTileData("Miasto 7", group3, 20000, 100000, 4000));
        final Tile tile13 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group3.png"),
                new Tile.CityTileData("Miasto 8", group3, 20000, 100000, 4000));
        final Tile tile14 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group3.png"),
                new Tile.CityTileData("Miasto 9", group3, 20000, 100000, 4000));
        final Tile tile15 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station.png"),
                new Tile.StationTileData("Stacja 2", stationGroup, 200000, 25000));
        final Tile tile16 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
//        final Tile tile17 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
//        final Tile tile18 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
        final Tile tile19 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"),
                new Tile.ChanceTileData());
        final Tile tile20 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"),
                new Tile.ChanceTileData());
        final Tile tile21 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/parking.png"),
                new Tile.PlaceholderTileData(0));
        final Tile tile22 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group4.png"),
                new Tile.CityTileData("Miasto 7", group4, 20000, 100000, 4000));
        final Tile tile23 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group4.png"),
                new Tile.CityTileData("Miasto 8", group4, 20000, 100000, 4000));
        final Tile tile24 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile25 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile26 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile27 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile28 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
//        final Tile tile29 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
//        final Tile tile30 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
        final Tile tile31 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/goToJail.png"),
                new Tile.GoToJailTileData(jailGroup));
        final Tile tile32 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group4.png"),
                new Tile.CityTileData("Miasto 9", group4, 20000, 100000, 4000));
        final Tile tile33 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile34 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile35 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile36 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile37 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
        final Tile tile38 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
                new Tile.CommunityChestTileData());
//        final Tile tile39 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
//        final Tile tile40 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"),
//                new Tile.CommunityChestTileData());
        final Tile tile2 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group1.png"),
                new Tile.CityTileData("Miasto 1", group1, 20000, 100000, 4000));
        final Tile tile3 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group1.png"),
                new Tile.CityTileData("Miasto 2", group1, 20000, 100000, 4000));
        final Tile tile4 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group1.png"),
                new Tile.CityTileData("Miasto 3", group1, 20000, 100000, 4000));
//        final Tile tile5 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/incomeTax.png"),
//                new Tile.IncomeTaxTileData(300000));
//        final Tile tile6 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"),
//                new Tile.ChanceTileData());
        final Tile tile7 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station.png"),
                new Tile.StationTileData("Stacja 1", stationGroup, 200000, 25000));
        final Tile tile8 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group2.png"),
                new Tile.CityTileData("Miasto 4", group2, 20000, 100000, 4000));
        final Tile tile9 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group2.png"),
                new Tile.CityTileData("Miasto 5", group2, 20000, 100000, 4000));

        setTiles(new Tile[]{tile1, tile2, tile3, tile4, tile7, tile8, tile9, tile10, tile11, tile12, tile13, tile14, tile15, tile16, tile19, tile20, tile21, tile22, tile23, tile24, tile25, tile26, tile27, tile28, tile31, tile32, tile33, tile34, tile35, tile36, tile37, tile38});
//        setTiles(new Tile[]{tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10, tile11, tile12, tile13, tile14, tile15, tile16, tile17, tile18, tile19, tile20, tile21, tile22, tile23, tile24, tile25, tile26, tile27, tile28, tile29, tile30, tile31, tile32, tile33, tile34, tile35, tile36, tile37, tile38, tile39, tile40});
    }
}
