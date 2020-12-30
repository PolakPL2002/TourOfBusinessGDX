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

        final CardDeck chanceDeck = new CardDeck()
                .addCard(new Card("Wyjdź bezpłatnie z więzienia", "", Card.CardType.GET_OUT_OF_JAIL))

                .addCard(new Card("Czas na podróż pociągiem!", "", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.STATION))
                .addCard(new Card("Czas na podróż!", "Idź do najbliższego środka transportu.", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.STATION))
                .addCard(new Card("Czas na podróż!", "Idź do najbliższego środka transportu.", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.STATION))
                .addCard(new Card("Awaria prądu!", "Idź do najbliższej elektrowni.", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.UTILITY))

                .addCard(new Card("Pobierz 500k$ za swój pierwszy rower!", "Składasz rowery w Pekinie. Pobierz 500k$ za swój pierwszy egzemplarz", Card.CardType.BALANCE_CHANGE, 500000))
                .addCard(new Card("Pobierz 1.5M$ pensji ze szkoły tańca!", "Twoja szkoła tańca brzucha w Kairze odnosi sukcesy! Pobierz pierwszą pensję 1.5M$.", Card.CardType.BALANCE_CHANGE, 1500000))
                .addCard(new Card("Zapłać 150k$", "Zapłać 150k$ za umycie i wypolerowanie 32 kapsuł na London Eye.", Card.CardType.BALANCE_CHANGE, -150000))

                .addCard(new Card("Przesuń się o 3 pola", "", Card.CardType.MOVE, 3))

                .addCard(new Card("Idź na start!", "", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.START))
                .addCard(new Card("Idż do Londynu!", "", Card.CardType.GO_TO, 21))
                .addCard(new Card("Idź do Krakowa!", "", Card.CardType.GO_TO, 37))

                .addCard(new Card("Idź do więzienia!", "Zostałeś złapany na międzynarodowym oszustwie! Idź do więzienia.", Card.CardType.GO_TO_TELEPORT, 30))
                .addCard(new Card("Leć do Monahium!", "", Card.CardType.GO_TO_TELEPORT, 18))

                .addCard(new Card("Zapłać każdemu z graczy po 500k$ za gotowanie.", "Urządziłeś turniej na najdziwniejsze przysmaki świata. Zapłać każdemu z graczy po 500k$ za gotowanie.", Card.CardType.PAY_PLAYERS, 500000))

                .addCard(new Card("Płacisz podwójny czynsz", "", Card.CardType.MODIFIED_RENT, 2000))
                .addCard(new Card("Płacisz połowę czynszu", "", Card.CardType.MODIFIED_RENT, 500));

        final CardDeck communityDeck = new CardDeck()
                .addCard(new Card("Wyjdź bezpłatnie z więzienia", "", Card.CardType.GET_OUT_OF_JAIL))

                .addCard(new Card("Zarobiłeś 1M$!", "Zorganizowałeś nowoczesną instalację w paryskim muzeum Georges Pompidou.", Card.CardType.BALANCE_CHANGE, 1000000))
                .addCard(new Card("Pobierz 500k$", "Dostałeś zwrot podatku.", Card.CardType.BALANCE_CHANGE, 500000))
                .addCard(new Card("Zarobiłeś 1M$!", "Zorganizowałeś festiwal muzyki szantowej na mazurach. Zarobiłeś aż 1M$!", Card.CardType.BALANCE_CHANGE, 1000000))
                .addCard(new Card("Zapłać 500k$", "Zapłać 500k$ za pilnowanie porządku na twojej plaży Bondi Beach w Sydney.", Card.CardType.BALANCE_CHANGE, -500000))
                .addCard(new Card("Zapłać 200k$", "Twój koronkowy strój na karnawał w Rio De Janeiro kosztował cię aż 200k$.", Card.CardType.BALANCE_CHANGE, -200000))
                .addCard(new Card("Pobierz 250k$", "Dostałeś pierwszą premię! Pobierz 250k$ za sprzedaż najdroższej sukni dostępnej w Paryżu.", Card.CardType.BALANCE_CHANGE, 250000))
                .addCard(new Card("Wydałeś 500k$", "Wydałeś 500k$ na świąteczne zakupy w Monachium.", Card.CardType.BALANCE_CHANGE, -500000))
                .addCard(new Card("Zarobiłeś 2M$", "Twoje biuro podróży przeżywa prawdziwe oblężenie! Zarobiłeś 2M$.", Card.CardType.BALANCE_CHANGE, 2000000))
                .addCard(new Card("Zarobiłeś 100k$", "Jesteś gospodarzem na imprezie karaoke w Japonii. Organizator płaci ci 100k$.", Card.CardType.BALANCE_CHANGE, 100000))
                .addCard(new Card("Zapłać 1M$", "Wyjechałeś do Dublina, by tam uczcić dzień św. Patryka. Zapłać za podróż 1M$.", Card.CardType.BALANCE_CHANGE, -1000000))
                .addCard(new Card("Otrzymujesz 1M$", "Dostajesz 1M$ za użyczenie jednego ze swoich alpejskich szczytów zapalonej grupie narciarzy.", Card.CardType.BALANCE_CHANGE, 1000000))

                .addCard(new Card("Idź na start!", "", Card.CardType.GO_TO_NEAREST_OF_TYPE, Tile.TileType.START))

                .addCard(new Card("Idź do więzienia!", "Zostałeś złapany na międzynarodowym oszustwie! Idź do więzienia.", Card.CardType.GO_TO_TELEPORT, 30))

                .addCard(new Card("Zbierz od każdego z graczy po 100k$.", "Urządzasz wielką imprezę w samym sercu Queenstown. Zbierz od każdego z graczy po 100k$ na ten cel.", Card.CardType.PAY_PLAYERS, -100000));
        setTiles(new Tile[]{
                new Tile(o, new Tile.StartTileData(2000000)),
                new Tile(o2, new Tile.CityTileData("Grenada", group1, 600000, 500000, 20000).addRent(100000).addRent(300000).addRent(900000).addRent(1600000).addRent(2500000)),
                new Tile(o3, new Tile.CommunityChestTileData(communityDeck)),
                new Tile(o4, new Tile.CityTileData("Madryt", group1, 600000, 500000, 40000).addRent(200000).addRent(600000).addRent(1800000).addRent(3200000).addRent(4500000)),
                new Tile(o5, new Tile.IncomeTaxTileData(2000000)),
                new Tile(o6, new Tile.StationTileData("Stacja na Bali", stationGroup, 2000000, 250000).addRent(500000).addRent(1000000).addRent(2000000)),
                new Tile(o7, new Tile.CityTileData("Hongkong", group2, 1000000, 500000, 60000).addRent(300000).addRent(900000).addRent(2700000).addRent(4000000).addRent(5500000)),
                new Tile(o8, new Tile.ChanceTileData(chanceDeck)),
                new Tile(o9, new Tile.CityTileData("Pekin", group2, 1000000, 500000, 60000).addRent(300000).addRent(900000).addRent(2700000).addRent(4000000).addRent(5500000)),
                new Tile(o10, new Tile.CityTileData("Szanghaj", group2, 1200000, 500000, 80000).addRent(400000).addRent(1000000).addRent(3000000).addRent(4500000).addRent(6000000)),
                new Tile(o11, new Tile.JailTileData(jailGroup, 500000, 3)),
                new Tile(o12, new Tile.CityTileData("Wenecja", group3, 1400000, 1000000, 100000).addRent(500000).addRent(1500000).addRent(4500000).addRent(6250000).addRent(7500000)),
                new Tile(o13, new Tile.UtilityTileData("Elektrownia", utilityGroup, 1500000, 40000).addMultiplier(100000)),
                new Tile(o14, new Tile.CityTileData("Mediolan", group3, 1400000, 1000000, 100000).addRent(500000).addRent(1500000).addRent(4500000).addRent(6250000).addRent(7500000)),
                new Tile(o15, new Tile.CityTileData("Rzym", group3, 1600000, 1000000, 120000).addRent(600000).addRent(1800000).addRent(5000000).addRent(7000000).addRent(9000000)),
                new Tile(o16, new Tile.StationTileData("Stacja na Cyprze", stationGroup, 2000000, 250000).addRent(500000).addRent(1000000).addRent(2000000)),
                new Tile(o17, new Tile.CityTileData("Hamburg", group4, 1800000, 1000000, 140000).addRent(700000).addRent(2000000).addRent(5500000).addRent(7500000).addRent(9500000)),
                new Tile(o3, new Tile.CommunityChestTileData(communityDeck)),
                new Tile(o18, new Tile.CityTileData("Monachium", group4, 1800000, 1000000, 140000).addRent(700000).addRent(2000000).addRent(5500000).addRent(7500000).addRent(9500000)),
                new Tile(o19, new Tile.CityTileData("Berlin", group4, 2000000, 1000000, 160000).addRent(800000).addRent(2200000).addRent(6000000).addRent(8000000).addRent(10000000)),
                new Tile(o20, new Tile.PlaceholderTileData(0)),
                new Tile(o21, new Tile.CityTileData("Londyn", group5, 2200000, 1500000, 180000).addRent(900000).addRent(2500000).addRent(7000000).addRent(8750000).addRent(10500000)),
                new Tile(o8, new Tile.ChanceTileData(chanceDeck)),
                new Tile(o22, new Tile.CityTileData("Manchester", group5, 2200000, 1500000, 180000).addRent(900000).addRent(2500000).addRent(7000000).addRent(8750000).addRent(10500000)),
                new Tile(o23, new Tile.CityTileData("Sydney", group5, 2400000, 1500000, 200000).addRent(1000000).addRent(3000000).addRent(7500000).addRent(9250000).addRent(11000000)),
                new Tile(o24, new Tile.StationTileData("Stacja w Dubaju", stationGroup, 2000000, 250000).addRent(500000).addRent(1000000).addRent(2000000)),
                new Tile(o25, new Tile.CityTileData("Chicago", group6, 2600000, 1500000, 220000).addRent(1100000).addRent(3300000).addRent(8000000).addRent(9750000).addRent(11500000)),
                new Tile(o26, new Tile.CityTileData("Las Vegas", group6, 2600000, 1500000, 220000).addRent(1100000).addRent(3300000).addRent(8000000).addRent(9750000).addRent(11500000)),
                new Tile(o27, new Tile.UtilityTileData("Wodociągi", utilityGroup, 1500000, 40000).addMultiplier(100000)),
                new Tile(o28, new Tile.CityTileData("Nowy Jork", group6, 2800000, 1500000, 240000).addRent(1200000).addRent(3600000).addRent(8500000).addRent(10250000).addRent(12000000)),
                new Tile(o29, new Tile.GoToJailTileData(jailGroup)),
                new Tile(o30, new Tile.CityTileData("Lyon", group7, 3000000, 2000000, 260000).addRent(1300000).addRent(3900000).addRent(9000000).addRent(11000000).addRent(12750000)),
                new Tile(o31, new Tile.CityTileData("Genewa", group7, 3000000, 2000000, 260000).addRent(1300000).addRent(3900000).addRent(9000000).addRent(11000000).addRent(12750000)),
                new Tile(o3, new Tile.CommunityChestTileData(communityDeck)),
                new Tile(o32, new Tile.CityTileData("Paryż", group7, 3200000, 2000000, 280000).addRent(1500000).addRent(4500000).addRent(10000000).addRent(12000000).addRent(14000000)),
                new Tile(o33, new Tile.StationTileData("Stacja w Nicei", stationGroup, 2000000, 250000).addRent(500000).addRent(1000000).addRent(2000000)),
                new Tile(o8, new Tile.ChanceTileData(chanceDeck)),
                new Tile(o34, new Tile.CityTileData("Kraków", group8, 3500000, 2000000, 350000).addRent(1750000).addRent(5000000).addRent(11000000).addRent(13000000).addRent(15000000)),
                new Tile(o35, new Tile.LuxuryTaxTileData(1000000)),
                new Tile(o36, new Tile.CityTileData("Warszawa", group8, 4000000, 2000000, 500000).addRent(2000000).addRent(6000000).addRent(14000000).addRent(17000000).addRent(20000000))
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
