package pl.greenmc.tob.game.map;

import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Tile {
    private final TileData data;
    private final Texture texture;
    private final TileType type;

    public Tile(@NotNull Texture texture, @NotNull StartTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.START;
    }

    public Tile(@NotNull Texture texture, @NotNull StationTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.STATION;
        data.tileGroup.addTile(this);
    }

    public Tile(@NotNull Texture texture, @NotNull CityTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CITY;
        data.tileGroup.addTile(this);
    }

    public Tile(@NotNull Texture texture, @NotNull UtilityTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.UTILITY;
        data.tileGroup.addTile(this);
    }

    public Tile(@NotNull Texture texture, @NotNull ChanceTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CHANCE;
    }

    public Tile(@NotNull Texture texture, @NotNull CommunityChestTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.COMMUNITY_CHEST;
    }

    public Tile(@NotNull Texture texture, @NotNull PlaceholderTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.PLACEHOLDER;
    }

    public Tile(@NotNull Texture texture, @NotNull JailTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.JAIL;
        data.tileGroup.addTile(this);
    }

    public Tile(@NotNull Texture texture, @NotNull GoToJailTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.GO_TO_JAIL;
        data.tileGroup.addTile(this);
    }

    public Tile(@NotNull Texture texture, @NotNull ChampionshipsTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.CHAMPIONSHIPS;
    }

    public Tile(@NotNull Texture texture, @NotNull TravelTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.TRAVEL;
    }

    public Tile(@NotNull Texture texture, @NotNull IncomeTaxTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.INCOME_TAX;
    }

    public Tile(@NotNull Texture texture, @NotNull LuxuryTaxTileData data) {
        this.texture = texture;
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
        this.data = data;
        type = TileType.LUXURY_TAX;
    }

    public TileData getData() {
        return data;
    }

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

    static class ChampionshipsTileData extends TileData {
        private final int cost;

        public ChampionshipsTileData(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }

    static class ChanceTileData extends TileData {
        public ChanceTileData() {
        }
    }

    static class CityTileData extends TileData {
        private final int improvementCost;
        private final String name;
        private final ArrayList<Integer> rents = new ArrayList<>();
        private final TileGroup tileGroup;
        private final int value;

        public CityTileData(@NotNull String name, @NotNull TileGroup tileGroup, int value, int improvementCost, int rent) {
            this.name = name;
            this.tileGroup = tileGroup;
            this.value = value;
            this.improvementCost = improvementCost;
            rents.add(rent);
        }

        public int getImprovementCost() {
            return improvementCost;
        }

        public String getName() {
            return name;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }

        public int getValue() {
            return value;
        }

        public CityTileData addRent(int rent) {
            rents.add(rent);
            return this;
        }

        public int getRent(int level) {
            if (level >= rents.size()) return rents.get(rents.size() - 1);
            return rents.get(level);
        }

        public int getMaxLevel() {
            return rents.size() - 1;
        }
    }

    static class CommunityChestTileData extends TileData {
        public CommunityChestTileData() {
        }
    }

    static class GoToJailTileData extends TileData {
        private final TileGroup tileGroup;

        public GoToJailTileData(@NotNull TileGroup tileGroup) {
            this.tileGroup = tileGroup;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }
    }

    static class IncomeTaxTileData extends TileData {
        private final int cost;

        public IncomeTaxTileData(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }

    static class JailTileData extends TileData {
        private final int bailMoney;
        private final int maxRounds;
        private final TileGroup tileGroup;

        public JailTileData(@NotNull TileGroup tileGroup, int bailMoney, int maxRounds) {
            this.tileGroup = tileGroup;
            this.bailMoney = bailMoney;
            this.maxRounds = maxRounds;
        }

        public int getBailMoney() {
            return bailMoney;
        }

        public int getMaxRounds() {
            return maxRounds;
        }

        public TileGroup getTileGroup() {
            return tileGroup;
        }
    }

    static class LuxuryTaxTileData extends TileData {
        private final int cost;

        public LuxuryTaxTileData(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }

    static class PlaceholderTileData extends TileData {
        private final int charge;

        public PlaceholderTileData(int charge) {
            this.charge = charge;
        }

        public int getCharge() {
            return charge;
        }
    }

    static class StartTileData extends TileData {
        private final int startMoney;

        public StartTileData(int startMoney) {
            this.startMoney = startMoney;
        }

        public int getStartMoney() {
            return startMoney;
        }
    }

    static class StationTileData extends TileData {
        private final String name;
        private final ArrayList<Integer> rents = new ArrayList<>();
        private final TileGroup tileGroup;
        private final int value;

        public StationTileData(@NotNull String name, @NotNull TileGroup tileGroup, int value, int rent) {
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

        public int getValue() {
            return value;
        }


        public StationTileData addRent(int rent) {
            rents.add(rent);
            return this;
        }

        public int getRent(int level) {
            if (level >= rents.size()) return rents.get(rents.size() - 1);
            return rents.get(level);
        }

        public int getMaxLevel() {
            return rents.size() - 1;
        }
    }

    private static abstract class TileData {
    }

    static class TileGroup {
        private final ArrayList<Tile> tiles = new ArrayList<>();

        public void addTile(@NotNull Tile tile) {
            tiles.add(tile);
        }

        public Tile[] getTiles() {
            return tiles.toArray(new Tile[0]);
        }
    }

    static class TravelTileData extends TileData {
        private final int cost;

        public TravelTileData(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }

    static class UtilityTileData extends TileData {
        private final ArrayList<Integer> multipliers = new ArrayList<>();
        private final String name;
        private final TileGroup tileGroup;
        private final int value;

        public UtilityTileData(@NotNull String name, @NotNull TileGroup tileGroup, int value, int multiplier) {
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

        public int getValue() {
            return value;
        }

        public UtilityTileData addMultiplier(int multiplier) {
            multipliers.add(multiplier);
            return this;
        }

        public int getMultiplier(int level) {
            if (level >= multipliers.size()) return multipliers.get(multipliers.size() - 1);
            return multipliers.get(level);
        }

        public int getMaxLevel() {
            return multipliers.size() - 1;
        }
    }
}
