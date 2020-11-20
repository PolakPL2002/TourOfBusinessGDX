package pl.greenmc.tob.game.map;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class TinyMap extends Map {
    public TinyMap() {
        super(TOB.getGame().getAssetManager().get("textures/maps/default/board.png"));

        final Tile.TileGroup group1 = new Tile.TileGroup();
        final Tile.TileGroup group3 = new Tile.TileGroup();
        final Tile.TileGroup group4 = new Tile.TileGroup();
        final Tile.TileGroup jailGroup = new Tile.TileGroup();
        final Tile tile1 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/start.png"),
                new Tile.StartTileData(300000));
        final Tile tile11 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/jail.png"),
                new Tile.JailTileData(jailGroup, 200000, 3));
        final Tile tile12 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group3.png"),
                new Tile.CityTileData("Miasto 7", group3, 20000, 100000, 4000));
        final Tile tile21 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/parking.png"),
                new Tile.PlaceholderTileData(0));
        final Tile tile22 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group4.png"),
                new Tile.CityTileData("Miasto 7", group4, 20000, 100000, 4000));
        final Tile tile31 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/goToJail.png"),
                new Tile.GoToJailTileData(jailGroup));
        final Tile tile32 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group4.png"),
                new Tile.CityTileData("Miasto 9", group4, 20000, 100000, 4000));
        final Tile tile2 = new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/group1.png"),
                new Tile.CityTileData("Miasto 1", group1, 20000, 100000, 4000));

        setTiles(new Tile[]{tile1, tile2, tile11, tile12, tile21, tile22, tile31, tile32,});
    }
}
