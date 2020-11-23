package pl.greenmc.tob.game.map;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class DefaultMap extends Map {
    //fixme In server mode: java.lang.NullPointerException
    //	at pl.greenmc.tob.game.map.DefaultMap.<init>(DefaultMap.java:7)
    //	at pl.greenmc.tob.game.Lobby.setLobbyState(Lobby.java:117)
    //	at pl.greenmc.tob.game.TourOfBusinessServer$1.onPacketReceived(TourOfBusinessServer.java:235)
    //	at pl.greenmc.tob.game.netty.server.ServerHandler.channelRead(ServerHandler.java:196)
    public DefaultMap() {
        super(TOB.getGame().getAssetManager().get("textures/maps/default/board.png"));

        final Tile.TileGroup group1 = new Tile.TileGroup();
        final Tile.TileGroup group2 = new Tile.TileGroup();
        final Tile.TileGroup group3 = new Tile.TileGroup();
        final Tile.TileGroup group4 = new Tile.TileGroup();
        final Tile.TileGroup group5 = new Tile.TileGroup();
        final Tile.TileGroup group6 = new Tile.TileGroup();
        final Tile.TileGroup group7 = new Tile.TileGroup();
        final Tile.TileGroup group8 = new Tile.TileGroup();
        final Tile.TileGroup jailGroup = new Tile.TileGroup();
        final Tile.TileGroup stationGroup = new Tile.TileGroup();
        final Tile.TileGroup utilityGroup = new Tile.TileGroup();


        setTiles(new Tile[]{
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/start.png"), new Tile.StartTileData(300000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city1.png"), new Tile.CityTileData("Grenada", group1, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"), new Tile.CommunityChestTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city2.png"), new Tile.CityTileData("Madryt", group1, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/incomeTax.png"), new Tile.IncomeTaxTileData(300000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station1.png"), new Tile.StationTileData("Stacja na Bali", stationGroup, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city3.png"), new Tile.CityTileData("Hongkong", group2, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"), new Tile.ChanceTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city4.png"), new Tile.CityTileData("Pekin", group2, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city5.png"), new Tile.CityTileData("Szanghaj", group2, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/jail.png"), new Tile.JailTileData(jailGroup, 1000, 3)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city6.png"), new Tile.CityTileData("Wenecja", group3, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/electricCompany.png"), new Tile.UtilityTileData("Elektrownia", utilityGroup, 1000, 10)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city7.png"), new Tile.CityTileData("Mediolan", group3, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city8.png"), new Tile.CityTileData("Rzym", group3, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station2.png"), new Tile.StationTileData("Stacja na Cyprze", stationGroup, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city9.png"), new Tile.CityTileData("Hamburg", group4, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"), new Tile.CommunityChestTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city10.png"), new Tile.CityTileData("Monachium", group4, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city11.png"), new Tile.CityTileData("Berlin", group4, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/parking.png"), new Tile.PlaceholderTileData(1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city12.png"), new Tile.CityTileData("Londyn", group5, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"), new Tile.ChanceTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city13.png"), new Tile.CityTileData("Manchester", group5, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city14.png"), new Tile.CityTileData("Sydney", group5, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station3.png"), new Tile.StationTileData("Stacja w Dubaju", stationGroup, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city15.png"), new Tile.CityTileData("Chicago", group6, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city16.png"), new Tile.CityTileData("Las Vegas", group6, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/waterWorks.png"), new Tile.UtilityTileData("Wodociągi", utilityGroup, 1000, 10)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city17.png"), new Tile.CityTileData("Nowy Jork", group6, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/goToJail.png"), new Tile.GoToJailTileData(jailGroup)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city18.png"), new Tile.CityTileData("Lyon", group7, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city19.png"), new Tile.CityTileData("Genewa", group7, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/communityChest.png"), new Tile.CommunityChestTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city20.png"), new Tile.CityTileData("Paryż", group7, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/station4.png"), new Tile.StationTileData("Stacja w Nicei", stationGroup, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/chance.png"), new Tile.ChanceTileData()),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city21.png"), new Tile.CityTileData("Kraków", group8, 1000, 1000, 1000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/luxuryTax.png"), new Tile.LuxuryTaxTileData(150000)),
                new Tile(TOB.getGame().getAssetManager().get("textures/maps/default/city22.png"), new Tile.CityTileData("Warszawa", group8, 1000, 1000, 1000))
        });
    }
}
