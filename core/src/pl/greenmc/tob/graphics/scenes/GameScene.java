package pl.greenmc.tob.graphics.scenes;

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
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.map.Map;
import pl.greenmc.tob.game.map.Tile;
import pl.greenmc.tob.graphics.Scene;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GameScene extends Scene {
    private final Map map;
    private final ArrayList<Model> tileModels = new ArrayList<>();
    public Model boardModel;
    public ArrayList<ModelInstance> instances = new ArrayList<>();
    public ModelBatch modelBatch;
    private OrthographicCamera cam;
    private Environment environment;

    public GameScene(Map map) {
        this.map = map;
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
        cam = new OrthographicCamera(4.5f / Gdx.graphics.getHeight() * Gdx.graphics.getWidth(), 4.5f);
        cam.position.set(-10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(new Color(.7f, .7f, .7f, 1), cam.direction));

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
            float offset = tileSizeBase * numTiles / 4 - idInRow * tileSizeBase * 2;
            if (row == 0) {
                if (bigTile) {
                    instance.transform.translate(-tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f, 0.065f, tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f);
                } else {
                    instance.transform.translate(-tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f, 0.065f, offset);
                }
            } else if (row == 1) {
                if (bigTile) {
                    instance.transform.translate(-tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f, 0.065f, -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f);
                } else {
                    instance.transform.translate(-offset, 0.065f, -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f);
                    instance.transform.rotate(new Vector3(0, 1, 0), 90);
                }
            } else if (row == 2) {
                if (bigTile) {
                    instance.transform.translate(tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f, 0.065f, -tileSizeBase * (numTiles / 4.0f + 1) - 0.0025f);
                } else {
                    instance.transform.translate(tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f, 0.065f, -offset);
                }
            } else if (row == 3) {
                if (bigTile) {
                    instance.transform.translate(tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f, 0.065f, tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f);
                } else {
                    instance.transform.translate(offset, 0.065f, tileSizeBase * (numTiles / 4.0f + 1) + 0.0025f);
                    instance.transform.rotate(new Vector3(0, 1, 0), 90);
                }
            }
            instances.add(instance);
            tileModels.add(model);
            i++;
        }
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

    @Override
    public void dispose() {
        modelBatch.dispose();
        boardModel.dispose();
        tileModels.forEach(Model::dispose);
    }
}
