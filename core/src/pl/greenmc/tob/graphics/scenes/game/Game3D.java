package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.graphics.Hitbox;
import pl.greenmc.tob.graphics.PolygonHitbox;
import pl.greenmc.tob.graphics.Scene;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

class Game3D extends Scene {
    private final HashMap<Tile, Hitbox> hitboxes = new HashMap<>();
    private final ArrayList<ModelInstance> instances = new ArrayList<>();
    private final Map map;
    private final ArrayList<Model> tileModels = new ArrayList<>();
    private Model boardModel;
    private OrthographicCamera cam;
    private Environment environment;
    private ModelBatch modelBatch;

    public Game3D(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public HashMap<Tile, Hitbox> getHitboxes() {
        return hitboxes;
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void setup() {
        modelBatch = new ModelBatch();
        setupCamera();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(new Color(.7f, .7f, .7f, 1), cam.direction));

        setupModels();
        generateHitboxes();
    }

    private void generateHitboxes() {
        hitboxes.clear();
        int numTiles = map.getTiles().length;
        float tileSizeBase = 5.2f / (numTiles / 4.0f + 3) / 2;
        int i = 0;
        for (Tile tile : map.getTiles()) {
            int idInRow = i % (numTiles / 4);
            boolean bigTile = idInRow == 0;
            int row = i / (numTiles / 4);

            float offset = tileSizeBase * numTiles / 4 - idInRow * tileSizeBase * 2;

            final Vector3 tileLocation = getTileLocation(numTiles, tileSizeBase, idInRow, bigTile, row);
            final Vector3 c1;
            final Vector3 c2;
            final Vector3 c3;
            final Vector3 c4;
            if (row % 2 == 1 && !bigTile) {
                c1 = cam.project(new Vector3(tileLocation).add(tileSizeBase - 0.0035f, 0.015f, 2 * tileSizeBase - 0.0035f));
                c2 = cam.project(new Vector3(tileLocation).add(-tileSizeBase + 0.0035f, 0.015f, 2 * tileSizeBase - 0.0035f));
                c3 = cam.project(new Vector3(tileLocation).add(-tileSizeBase + 0.0035f, 0.015f, -2 * tileSizeBase + 0.0035f));
                c4 = cam.project(new Vector3(tileLocation).add(tileSizeBase - 0.0035f, 0.015f, -2 * tileSizeBase + 0.0035f));
            } else {
                c1 = cam.project(new Vector3(tileLocation).add(2 * tileSizeBase - 0.0035f, 0.015f, (bigTile ? 2 * tileSizeBase : tileSizeBase) - 0.0035f));
                c2 = cam.project(new Vector3(tileLocation).add(-2 * tileSizeBase + 0.0035f, 0.015f, (bigTile ? 2 * tileSizeBase : tileSizeBase) - 0.0035f));
                c3 = cam.project(new Vector3(tileLocation).add(-2 * tileSizeBase + 0.0035f, 0.015f, -(bigTile ? 2 * tileSizeBase : tileSizeBase) + 0.0035f));
                c4 = cam.project(new Vector3(tileLocation).add(2 * tileSizeBase - 0.0035f, 0.015f, -(bigTile ? 2 * tileSizeBase : tileSizeBase) + 0.0035f));
            }
            final Vector2 p1 = new Vector2(c1.x, c1.y);
            final Vector2 p2 = new Vector2(c2.x, c2.y);
            final Vector2 p3 = new Vector2(c3.x, c3.y);
            final Vector2 p4 = new Vector2(c4.x, c4.y);
            hitboxes.put(tile, new PolygonHitbox(p1, p2, p3, p4));
            i++;
        }
    }

    private void setupModels() {
        ModelBuilder modelBuilder = new ModelBuilder();
        boardModel = createModel(modelBuilder, 2.6f, 0.05f, 2.6f,
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                new Material(TextureAttribute.createDiffuse(map.getTexture())),
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                new Material(ColorAttribute.createDiffuse(Color.GRAY)));

        ModelInstance instance;
        instance = new ModelInstance(boardModel);
        instances.add(instance);

        int numTiles = map.getTiles().length;
        float tileSizeBase = 5.2f / (numTiles / 4.0f + 3) / 2;

        int i = 0;
        for (Tile tile : map.getTiles()) {
            int idInRow = i % (numTiles / 4);
            boolean bigTile = idInRow == 0;
            int row = i / (numTiles / 4);
            Model model;
            model = createModel(modelBuilder, 2 * tileSizeBase - 0.0025f, 0.015f, (bigTile ? 2 * tileSizeBase : tileSizeBase) - 0.0025f,
                    new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    new Material(TextureAttribute.createDiffuse(tile.getTexture())),
                    new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                    new Material(ColorAttribute.createDiffuse(Color.GRAY)));
            instance = new ModelInstance(model);
            instance.transform.translate(getTileLocation(numTiles, tileSizeBase, idInRow, bigTile, row));
            if (row % 2 == 1 && !bigTile)
                instance.transform.rotate(new Vector3(0, 1, 0), 90);
            instances.add(instance);
            tileModels.add(model);
            i++;
        }
    }

    @NotNull
    private Vector3 getTileLocation(int numTiles, float tileSizeBase, int idInRow, boolean bigTile, int row) {
        float offset = tileSizeBase * numTiles / 4 - idInRow * tileSizeBase * 2;
        float x = 0, y = 0.065f, z = 0;
        if (row == 0) {
            x = -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f;
            if (bigTile) {
                z = tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f;
            } else {
                z = offset;
            }
        } else if (row == 1) {
            if (bigTile) {
                x = -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f;
            } else {
                x = -offset;
            }
            z = -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f;
        } else if (row == 2) {
            x = tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f;
            if (bigTile) {
                z = -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f;
            } else {
                z = -offset;
            }
        } else if (row == 3) {
            if (bigTile) {
                x = tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f;
            } else {
                x = offset;
            }
            z = tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f;
        }
        return new Vector3(x, y, z);
    }

    private Model createModel(@NotNull ModelBuilder modelBuilder, float scaleX, float scaleY, float scaleZ, Material materialFront, Material materialBack, Material materialTop, Material materialBottom, Material materialLeft, Material materialRight) {
        modelBuilder.begin();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        modelBuilder.part("front", GL20.GL_TRIANGLES, attr, materialFront)
                .rect(-scaleX, -scaleY, -scaleZ, -scaleX, scaleY, -scaleZ, scaleX, scaleY, -scaleZ, scaleX, -scaleY, -scaleZ, 0, 0, -1f);
        modelBuilder.part("back", GL20.GL_TRIANGLES, attr, materialBack)
                .rect(-scaleX, scaleY, scaleZ, -scaleX, -scaleY, scaleZ, scaleX, -scaleY, scaleZ, scaleX, scaleY, scaleZ, 0, 0, 1f);
        modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, materialBottom)
                .rect(-scaleX, -scaleY, scaleZ, -scaleX, -scaleY, -scaleZ, scaleX, -scaleY, -scaleZ, scaleX, -scaleY, scaleZ, 0, -1f, 0);
        modelBuilder.part("top", GL20.GL_TRIANGLES, attr, materialTop)
                .rect(-scaleX, scaleY, -scaleZ, -scaleX, scaleY, scaleZ, scaleX, scaleY, scaleZ, scaleX, scaleY, -scaleZ, 0, 1f, 0);
        modelBuilder.part("left", GL20.GL_TRIANGLES, attr, materialLeft)
                .rect(-scaleX, -scaleY, scaleZ, -scaleX, scaleY, scaleZ, -scaleX, scaleY, -scaleZ, -scaleX, -scaleY, -scaleZ, -1f, 0, 0);
        modelBuilder.part("right", GL20.GL_TRIANGLES, attr, materialRight)
                .rect(scaleX, -scaleY, -scaleZ, scaleX, scaleY, -scaleZ, scaleX, scaleY, scaleZ, scaleX, -scaleY, scaleZ, 1f, 0, 0);
        return modelBuilder.end();
    }

    private void setupCamera() {
        if (Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() < 1.67f)
            cam = new OrthographicCamera(4.5f * 1.67f, 4.5f * 1.67f / Gdx.graphics.getWidth() * Gdx.graphics.getHeight());
        else
            cam = new OrthographicCamera(4.5f / Gdx.graphics.getHeight() * Gdx.graphics.getWidth(), 4.5f);
        cam.position.set(-10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
    }

    @Override
    public void resize(int width, int height) {
        modelBatch.dispose();
        modelBatch = new ModelBatch();
        setupCamera();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        boardModel.dispose();
        tileModels.forEach(Model::dispose);
    }
}
