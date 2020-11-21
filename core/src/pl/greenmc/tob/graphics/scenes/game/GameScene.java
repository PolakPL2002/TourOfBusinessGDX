package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GameScene extends Scene implements Interactable {
    private final Map map;
    //    private SpriteBatch batch;
    private Game3D game3D;
    private GamePlayersStats gamePlayersStats;
    private Texture texture;
//    private HashMap<Tile, Texture> textures = new HashMap<>();

    public GameScene(Map map) {
        this.map = map;
    }

    @Override
    public void onMouseDown() {

    }

    @Override
    public void onMouseMove(int x, int y) {
        gamePlayersStats.onMouseMove(x, y);
//        final HashMap<Tile, Hitbox> hitboxes = game3D.getHitboxes();
//        texture = null;
//        for (Tile tile : hitboxes.keySet()) {
//            if (hitboxes.get(tile).testMouseCoordinates(x, y)) {
//                texture = textures.get(tile);
//                break;
//            }
//        }
    }

    @Override
    public void onMouseUp() {

    }

    @Override
    public void onScroll(float x, float y) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(GlobalTheme.backgroundColor.r, GlobalTheme.backgroundColor.g, GlobalTheme.backgroundColor.b, GlobalTheme.backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        game3D.render();
        gamePlayersStats.render();
//        if (texture != null) {
//            batch.begin();
//            batch.draw(texture, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
//            batch.end();
//        }
    }

    @Override
    public void setup() {
        game3D = new Game3D(map);
        game3D.setup();
        gamePlayersStats = new GamePlayersStats();
        gamePlayersStats.setup();
//        batch = new SpriteBatch();
//        final HashMap<Tile, Hitbox> hitboxes = game3D.getHitboxes();
//        for (Tile tile : hitboxes.keySet()) {
//            textures.put(tile, hitboxes.get(tile).getHitboxOverlay(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
//        }
    }

    @Override
    public void resize(int width, int height) {
        game3D.resize(width, height);
        gamePlayersStats.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        game3D.dispose();
        gamePlayersStats.dispose();
    }
}
