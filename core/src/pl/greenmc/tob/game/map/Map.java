package pl.greenmc.tob.game.map;

import com.badlogic.gdx.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class Map {
    private final Texture texture;
    private final ArrayList<Tile> tiles = new ArrayList<>();

    public Map(@NotNull Texture texture, @NotNull Tile[] tiles) {
        this(texture);
        setTiles(tiles);
    }

    Map(@Nullable Texture texture) {
        this.texture = texture;
        if (texture != null)
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.MipMapLinearLinear);
    }

    @Nullable
    public Texture getTexture() {
        return texture;
    }

    public Tile[] getTiles() {
        return tiles.toArray(new Tile[0]);
    }

    void setTiles(@NotNull Tile[] tiles) {
        this.tiles.addAll(Arrays.asList(tiles));
        validateTiles();
    }

    private void validateTiles() {
        //Requirements:
        // - Total is multiple of 4 and in range <8; 40>
        // - Exactly 1 START
        // - Each JAIL has at least 1 GO_TO_JAIL in group
        // - Each GO_TO_JAIL has exactly 1 JAIL in group
        // - No group contains more that one type of following: CITY, STATION, UTILITY
        // - No group that contains JAIL, GO_TO_JAIL cannot contain CITY, STATION, UTILITY and vice versa
        // - No tile is duplicated
        if (tiles.size() % 4 != 0 || tiles.size() > 40 || tiles.size() < 8)
            throw new RuntimeException("Number of tiles is invalid.");
        int numStart = 0;
        for (Tile tile : tiles) {
            if (tile.getType() == Tile.TileType.START) numStart++;
            else if (tile.getType() == Tile.TileType.JAIL) {
                int numJail = 0;
                int numGTJ = 0;
                for (Tile tile1 : ((Tile.JailTileData) tile.getData()).getTileGroup().getTiles()) {
                    if (tile1.getType() == Tile.TileType.JAIL) numJail++;
                    else if (tile1.getType() == Tile.TileType.GO_TO_JAIL) numGTJ++;
                    else throw new RuntimeException("Group of jail contains non-(JAIL/GO_TO_JAIL) tile.");
                }
                if (numJail != 1) throw new RuntimeException("Group of JAIL contains invalid number of JAIL.");
                if (numGTJ < 1) throw new RuntimeException("Group of JAIL contains invalid number of GO_TO_JAIL.");
            } else if (tile.getType() == Tile.TileType.GO_TO_JAIL) {
                int numJail = 0;
                int numGTJ = 0;
                for (Tile tile1 : ((Tile.GoToJailTileData) tile.getData()).getTileGroup().getTiles()) {
                    if (tile1.getType() == Tile.TileType.JAIL) numJail++;
                    else if (tile1.getType() == Tile.TileType.GO_TO_JAIL) numGTJ++;
                    else throw new RuntimeException("Group of GO_TO_JAIL contains non-(JAIL/GO_TO_JAIL) tile.");
                }
                if (numJail != 1) throw new RuntimeException("Group of GO_TO_JAIL contains invalid number of JAIL.");
                if (numGTJ < 1)
                    throw new RuntimeException("Group of GO_TO_JAIL contains invalid number of GO_TO_JAIL.");
            } else if (tile.getType() == Tile.TileType.CITY) {
                for (Tile tile1 : ((Tile.CityTileData) tile.getData()).getTileGroup().getTiles()) {
                    if (tile1.getType() != Tile.TileType.CITY)
                        throw new RuntimeException("Group of CITY contains non-CITY tile.");
                }
            } else if (tile.getType() == Tile.TileType.STATION) {
                for (Tile tile1 : ((Tile.StationTileData) tile.getData()).getTileGroup().getTiles()) {
                    if (tile1.getType() != Tile.TileType.STATION)
                        throw new RuntimeException("Group of STATION contains non-STATION tile.");
                }
            } else if (tile.getType() == Tile.TileType.UTILITY) {
                for (Tile tile1 : ((Tile.UtilityTileData) tile.getData()).getTileGroup().getTiles()) {
                    if (tile1.getType() != Tile.TileType.UTILITY)
                        throw new RuntimeException("Group of UTILITY contains non-UTILITY tile.");
                }
            }
            int numEquals = 0;
            for (Tile tile1 : tiles) {
                if (tile == tile1) numEquals++;
                if (numEquals > 1) throw new RuntimeException("A tile is duplicated.");
            }
        }
        if (numStart != 1) throw new RuntimeException("There is invalid number of START.");
    }

    @Nullable
    protected static Texture loadTexture(String s) {
        if (TOB == null) {
            //Server mode
            return null;
        }
        return TOB.getGame().getAssetManager().get(s);
    }
}
