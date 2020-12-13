package pl.greenmc.tob.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;

public class DefaultMap extends Map {
    public DefaultMap(@NotNull Boolean headless) {
        super(headless ? null : loadTexture("textures/maps/default/board.png"));
        if (headless)
            setupTextureless();
        else
            setupTextured();
    }

    private void setupTextureless() {
        setupTiles(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private void setupTiles(Texture o, Texture o2, Texture o3, Texture o4, Texture o5, Texture o6, Texture o7, Texture o8,
                            Texture o9, Texture o10, Texture o11, Texture o12, Texture o13, Texture o14, Texture o15, Texture o16,
                            Texture o17, Texture o18, Texture o19, Texture o20, Texture o21, Texture o22, Texture o23, Texture o24,
                            Texture o25, Texture o26, Texture o27, Texture o28, Texture o29, Texture o30, Texture o31, Texture o32,
                            Texture o33, Texture o34, Texture o35, Texture o36) {
        final Tile.TileGroup group1 = new Tile.TileGroup("Grupa brązowa", new Color(144 / 255f, 103 / 255f, 64 / 255f, 1));
        final Tile.TileGroup group2 = new Tile.TileGroup("Grupa błękitna", new Color(79 / 255f, 198 / 255f, 228 / 255f, 1));
        final Tile.TileGroup group3 = new Tile.TileGroup("Grupa różowa", new Color(221 / 255f, 64 / 255f, 138 / 255f, 1));
        final Tile.TileGroup group4 = new Tile.TileGroup("Grupa pomarańczowa", new Color(235 / 255f, 168 / 255f, 64 / 255f, 1));
        final Tile.TileGroup group5 = new Tile.TileGroup("Grupa czerwona", new Color(235 / 255f, 64 / 255f, 64 / 255f, 1));
        final Tile.TileGroup group6 = new Tile.TileGroup("Grupa zółta", new Color(235 / 255f, 221 / 255f, 64 / 255f, 1));
        final Tile.TileGroup group7 = new Tile.TileGroup("Grupa zielona", new Color(66 / 255f, 156 / 255f, 64 / 255f, 1));
        final Tile.TileGroup group8 = new Tile.TileGroup("Grupa niebieska", new Color(64 / 255f, 74 / 255f, 156 / 255f, 1));
        final Tile.TileGroup jailGroup = new Tile.TileGroup("Grupa więzienna", Color.BLACK);
        final Tile.TileGroup stationGroup = new Tile.TileGroup("Stacje", Color.BLACK);
        final Tile.TileGroup utilityGroup = new Tile.TileGroup("Media", Color.BLACK);

        setTiles(new Tile[]{
                new Tile(o, new Tile.StartTileData(300000)),
                new Tile(o2, new Tile.CityTileData("Grenada", group1, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o3, new Tile.CommunityChestTileData()),
                new Tile(o4, new Tile.CityTileData("Madryt", group1, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o5, new Tile.IncomeTaxTileData(300000)),
                new Tile(o6, new Tile.StationTileData("Stacja na Bali", stationGroup, 1000, 1000)),
                new Tile(o7, new Tile.CityTileData("Hongkong", group2, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o8, new Tile.ChanceTileData()),
                new Tile(o9, new Tile.CityTileData("Pekin", group2, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o10, new Tile.CityTileData("Szanghaj", group2, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o11, new Tile.JailTileData(jailGroup, 1000, 3)),
                new Tile(o12, new Tile.CityTileData("Wenecja", group3, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o13, new Tile.UtilityTileData("Elektrownia", utilityGroup, 1000, 10)),
                new Tile(o14, new Tile.CityTileData("Mediolan", group3, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o15, new Tile.CityTileData("Rzym", group3, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o16, new Tile.StationTileData("Stacja na Cyprze", stationGroup, 1000, 1000)),
                new Tile(o17, new Tile.CityTileData("Hamburg", group4, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o3, new Tile.CommunityChestTileData()),
                new Tile(o18, new Tile.CityTileData("Monachium", group4, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o19, new Tile.CityTileData("Berlin", group4, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o20, new Tile.PlaceholderTileData(1000)),
                new Tile(o21, new Tile.CityTileData("Londyn", group5, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o8, new Tile.ChanceTileData()),
                new Tile(o22, new Tile.CityTileData("Manchester", group5, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o23, new Tile.CityTileData("Sydney", group5, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o24, new Tile.StationTileData("Stacja w Dubaju", stationGroup, 1000, 1000)),
                new Tile(o25, new Tile.CityTileData("Chicago", group6, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o26, new Tile.CityTileData("Las Vegas", group6, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o27, new Tile.UtilityTileData("Wodociągi", utilityGroup, 1000, 10)),
                new Tile(o28, new Tile.CityTileData("Nowy Jork", group6, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o29, new Tile.GoToJailTileData(jailGroup)),
                new Tile(o30, new Tile.CityTileData("Lyon", group7, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o31, new Tile.CityTileData("Genewa", group7, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o3, new Tile.CommunityChestTileData()),
                new Tile(o32, new Tile.CityTileData("Paryż", group7, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o33, new Tile.StationTileData("Stacja w Nicei", stationGroup, 1000, 1000)),
                new Tile(o8, new Tile.ChanceTileData()),
                new Tile(o34, new Tile.CityTileData("Kraków", group8, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000)),
                new Tile(o35, new Tile.LuxuryTaxTileData(150000)),
                new Tile(o36, new Tile.CityTileData("Warszawa", group8, 1000, 1000, 1000).addRent(2000).addRent(3000).addRent(4000).addRent(5000).addRent(6000))
        });
    }

    private void setupTextured() {
        setupTiles(loadTexture("textures/maps/default/start.png"),
                loadTexture("textures/maps/default/city1.png"),
                loadTexture("textures/maps/default/communityChest.png"),
                loadTexture("textures/maps/default/city2.png"),
                loadTexture("textures/maps/default/incomeTax.png"),
                loadTexture("textures/maps/default/station1.png"),
                loadTexture("textures/maps/default/city3.png"),
                loadTexture("textures/maps/default/chance.png"),
                loadTexture("textures/maps/default/city4.png"),
                loadTexture("textures/maps/default/city5.png"),
                loadTexture("textures/maps/default/jail.png"),
                loadTexture("textures/maps/default/city6.png"),
                loadTexture("textures/maps/default/electricCompany.png"),
                loadTexture("textures/maps/default/city7.png"),
                loadTexture("textures/maps/default/city8.png"),
                loadTexture("textures/maps/default/station2.png"),
                loadTexture("textures/maps/default/city9.png"),
                loadTexture("textures/maps/default/city10.png"),
                loadTexture("textures/maps/default/city11.png"),
                loadTexture("textures/maps/default/parking.png"),
                loadTexture("textures/maps/default/city12.png"),
                loadTexture("textures/maps/default/city13.png"),
                loadTexture("textures/maps/default/city14.png"),
                loadTexture("textures/maps/default/station3.png"),
                loadTexture("textures/maps/default/city15.png"),
                loadTexture("textures/maps/default/city16.png"),
                loadTexture("textures/maps/default/waterWorks.png"),
                loadTexture("textures/maps/default/city17.png"),
                loadTexture("textures/maps/default/goToJail.png"),
                loadTexture("textures/maps/default/city18.png"),
                loadTexture("textures/maps/default/city19.png"),
                loadTexture("textures/maps/default/city20.png"),
                loadTexture("textures/maps/default/station4.png"),
                loadTexture("textures/maps/default/city21.png"),
                loadTexture("textures/maps/default/luxuryTax.png"),
                loadTexture("textures/maps/default/city22.png"));
    }

    public DefaultMap() {
        super(loadTexture("textures/maps/default/board.png"));
        setupTextured();
    }
}
