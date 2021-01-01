package pl.greenmc.tob.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Tile {
    private final TileData data;
    private final Texture texture;
    private final TileType type;

    public Tile(@Nullable Texture texture, @NotNull StartTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.START;
    }

    public Tile(@Nullable Texture texture, @NotNull StationTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.STATION;
        data.tileGroup.addTile(this);
    }

    public Tile(@Nullable Texture texture, @NotNull CityTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CITY;
        data.tileGroup.addTile(this);
    }

    public Tile(@Nullable Texture texture, @NotNull UtilityTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.UTILITY;
        data.tileGroup.addTile(this);
    }

    public Tile(@Nullable Texture texture, @NotNull ChanceTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CHANCE;
    }

    public Tile(@Nullable Texture texture, @NotNull CommunityChestTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.COMMUNITY_CHEST;
    }

    public Tile(@Nullable Texture texture, @NotNull PlaceholderTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.PLACEHOLDER;
    }

    public Tile(@Nullable Texture texture, @NotNull JailTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.JAIL;
        data.tileGroup.addTile(this);
    }

    public Tile(@Nullable Texture texture, @NotNull GoToJailTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.GO_TO_JAIL;
        data.tileGroup.addTile(this);
    }

    public Tile(@Nullable Texture texture, @NotNull ChampionshipsTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CHAMPIONSHIPS;
    }

    public Tile(@Nullable Texture texture, @NotNull TravelTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.TRAVEL;
    }

    public Tile(@Nullable Texture texture, @NotNull IncomeTaxTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.INCOME_TAX;
    }

    public Tile(@Nullable Texture texture, @NotNull LuxuryTaxTileData data) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.LUXURY_TAX;
    }

    public TileData getData() {
        return data;
    }

    @Nullable
    public Texture getTexture() {
        return texture;
    }

    public TileType getType() {
        return type;
    }

    public enum TileType {
        START,
        CITY,
        STATION,
        UTILITY,
        CHANCE,
        COMMUNITY_CHEST,
        PLACEHOLDER,
        JAIL,
        GO_TO_JAIL,
        CHAMPIONSHIPS,
        TRAVEL,
        INCOME_TAX,
        LUXURY_TAX
    }

    public static class ChampionshipsTileData extends TileData {
        private final long cost;

        public ChampionshipsTileData(long cost) {
            this.cost = cost;
        }

        public long getCost() {
            return cost;
        }
    }

    public static class ChanceTileData extends TileData {
        private final CardDeck deck;

        public ChanceTileData(@NotNull CardDeck deck) {
            this.deck = deck;
        }

        @NotNull
        public CardDeck getDeck() {
            return deck;
        }
    }

    public static class CityTileData extends TileData {
        private final long improvementCost;
        private final String name;
        private final ArrayList<Long> rents = new ArrayList<>();
        private final TileGroup tileGroup;
        private final long value;

        public CityTileData(@NotNull String name, @NotNull TileGroup tileGroup, long value, long improvementCost, long rent) {
            this.name = name;
            this.tileGroup = tileGroup;
            this.value = value;
            this.improvementCost = improvementCost;
            rents.add(rent);
        }

        public long getImprovementCost() {
            return improvementCost;
        }

        public String getName() {
            return name;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }

        public long getValue() {
            return value;
        }

        public CityTileData addRent(long rent) {
            rents.add(rent);
            return this;
        }

        public long getRent(int level) {
            if (level >= rents.size()) return rents.get(rents.size() - 1);
            return rents.get(level);
        }

        public int getMaxLevel() {
            return rents.size() - 1;
        }
    }

    public static class CommunityChestTileData extends TileData {
        private final CardDeck deck;

        public CommunityChestTileData(@NotNull CardDeck deck) {
            this.deck = deck;
        }

        @NotNull
        public CardDeck getDeck() {
            return deck;
        }
    }

    public static class GoToJailTileData extends TileData {
        private final TileGroup tileGroup;

        public GoToJailTileData(@NotNull TileGroup tileGroup) {
            this.tileGroup = tileGroup;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }
    }

    public static class IncomeTaxTileData extends TileData {
        private final long cost;

        public IncomeTaxTileData(long cost) {
            this.cost = cost;
        }

        public long getCost() {
            return cost;
        }
    }

    public static class JailTileData extends TileData {
        private final long bailMoney;
        private final int maxRounds;
        private final TileGroup tileGroup;

        public JailTileData(@NotNull TileGroup tileGroup, long bailMoney, int maxRounds) {
            this.tileGroup = tileGroup;
            this.bailMoney = bailMoney;
            this.maxRounds = maxRounds;
        }

        public long getBailMoney() {
            return bailMoney;
        }

        public int getMaxRounds() {
            return maxRounds;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }
    }

    public static class LuxuryTaxTileData extends TileData {
        private final long cost;

        public LuxuryTaxTileData(long cost) {
            this.cost = cost;
        }

        public long getCost() {
            return cost;
        }
    }

    public static class PlaceholderTileData extends TileData {
        private final long charge;

        public PlaceholderTileData(long charge) {
            this.charge = charge;
        }

        public long getCharge() {
            return charge;
        }
    }

    public static class StartTileData extends TileData {
        private final long startMoney;

        public StartTileData(long startMoney) {
            this.startMoney = startMoney;
        }

        public long getStartMoney() {
            return startMoney;
        }
    }

    public static class StationTileData extends TileData {
        private final String name;
        private final ArrayList<Long> rents = new ArrayList<>();
        private final TileGroup tileGroup;
        private final long value;

        public StationTileData(@NotNull String name, @NotNull TileGroup tileGroup, long value, long rent) {
            this.name = name;
            this.tileGroup = tileGroup;
            this.value = value;
            this.rents.add(rent);
        }

        public String getName() {
            return name;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }

        public long getValue() {
            return value;
        }


        public StationTileData addRent(long rent) {
            rents.add(rent);
            return this;
        }

        public long getRent(int level) {
            level--;
            if (level < 0) level = 0;
            if (level >= rents.size()) return rents.get(rents.size() - 1);
            return rents.get(level);
        }

        public int getMaxLevel() {
            return rents.size();
        }
    }

    private static abstract class TileData {
    }

    public static class TileGroup {
        private final Color color;
        private final String name;
        private final ArrayList<Tile> tiles = new ArrayList<>();

        public TileGroup(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public void addTile(@NotNull Tile tile) {
            tiles.add(tile);
        }

        public Tile[] getTiles() {
            return tiles.toArray(new Tile[0]);
        }
    }

    public static class TravelTileData extends TileData {
        private final long cost;

        public TravelTileData(long cost) {
            this.cost = cost;
        }

        public long getCost() {
            return cost;
        }
    }

    public static class UtilityTileData extends TileData {
        private final ArrayList<Long> multipliers = new ArrayList<>();
        private final String name;
        private final TileGroup tileGroup;
        private final long value;

        public UtilityTileData(@NotNull String name, @NotNull TileGroup tileGroup, long value, long multiplier) {
            this.name = name;
            this.tileGroup = tileGroup;
            this.value = value;
            multipliers.add(multiplier);
        }

        public String getName() {
            return name;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }

        public long getValue() {
            return value;
        }

        public UtilityTileData addMultiplier(long multiplier) {
            multipliers.add(multiplier);
            return this;
        }

        public long getMultiplier(int level) {
            level--;
            if (level < 0) level = 0;
            if (level >= multipliers.size()) return multipliers.get(multipliers.size() - 1);
            return multipliers.get(level);
        }

        public int getMaxLevel() {
            return multipliers.size();
        }
    }
}
