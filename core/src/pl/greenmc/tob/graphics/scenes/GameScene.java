package pl.greenmc.tob.graphics.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Scene;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GameScene extends Scene {
    public ArrayList<ModelInstance> instances = new ArrayList<>();
    public Model model;
    public ModelBatch modelBatch;
    private OrthographicCamera cam;
    private Environment environment;

    @Override
    public void render() {
//        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void setup() {
        modelBatch = new ModelBatch();
        cam = new OrthographicCamera(8.0f, 8.0f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .3f, .3f, .4f, 1f));
        environment.add(new DirectionalLight().set(new Color(.7f, .7f, .7f, 1), cam.direction));

        ModelBuilder modelBuilder = new ModelBuilder();
        model = createModel(modelBuilder, 2.6f, 0.05f, 2.6f);
        model = modelBuilder.createBox(5.2f, .1f, 5.2f,
                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance instance;
        instance = new ModelInstance(model);
        instances.add(instance);
        model = createModel(modelBuilder, 0.3975f, 0.015f, 0.3975f);
        instance = new ModelInstance(model);
        instance.transform.translate(2.2025f, 0.065f, 2.2025f);
        instances.add(instance);
        instance = new ModelInstance(model);
        instance.transform.translate(-2.2025f, 0.065f, 2.2025f);
        instances.add(instance);
        instance = new ModelInstance(model);
        instance.transform.translate(-2.2025f, 0.065f, -2.2025f);
        instances.add(instance);
        instance = new ModelInstance(model);
        instance.transform.translate(2.2025f, 0.065f, -2.2025f);
        instances.add(instance);
        model = createModel(modelBuilder, 0.195f, 0.015f, 0.3975f);
        for (int i = 0; i < 10; i++) {
            instance = new ModelInstance(model);
            instance.transform.translate(-2f + i * 0.4f, 0.065f, 2.2025f);
            instances.add(instance);
        }
        for (int i = 0; i < 10; i++) {
            instance = new ModelInstance(model);
            instance.transform.translate(-2f + i * 0.4f, 0.065f, -2.2025f);
            instances.add(instance);
        }
        model = createModel(modelBuilder, 0.3975f, 0.015f, 0.195f);
        for (int i = 0; i < 10; i++) {
            instance = new ModelInstance(model);
            instance.transform.translate(2.2025f, 0.065f, -2f + i * 0.4f);
            instances.add(instance);
        }
        for (int i = 0; i < 10; i++) {
            instance = new ModelInstance(model);
            instance.transform.translate(-2.2025f, 0.065f, -2f + i * 0.4f);
            instances.add(instance);
        }
    }

    private Model createModel(@NotNull ModelBuilder modelBuilder, float scaleX, float scaleY, float scaleZ) {
        modelBuilder.begin();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        modelBuilder.part("front", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .rect(-scaleX, -scaleY, -scaleZ, -scaleX, scaleY, -scaleZ, scaleX, scaleY, -scaleZ, scaleX, -scaleY, -scaleZ, 0, 0, -1f);
        modelBuilder.part("back", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .rect(-scaleX, scaleY, scaleZ, -scaleX, -scaleY, scaleZ, scaleX, -scaleY, scaleZ, scaleX, scaleY, scaleZ, 0, 0, 1f);
        modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .rect(-scaleX, -scaleY, scaleZ, -scaleX, -scaleY, -scaleZ, scaleX, -scaleY, -scaleZ, scaleX, -scaleY, scaleZ, 0, -1f, 0);
        modelBuilder.part("top", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .rect(-scaleX, scaleY, -scaleZ, -scaleX, scaleY, scaleZ, scaleX, scaleY, scaleZ, scaleX, scaleY, -scaleZ, 0, 1f, 0);
        modelBuilder.part("left", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.ORANGE)))
                .rect(-scaleX, -scaleY, scaleZ, -scaleX, scaleY, scaleZ, -scaleX, scaleY, -scaleZ, -scaleX, -scaleY, -scaleZ, -1f, 0, 0);
        modelBuilder.part("right", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.BLACK)))
                .rect(scaleX, -scaleY, -scaleZ, scaleX, scaleY, -scaleZ, scaleX, scaleY, scaleZ, scaleX, -scaleY, scaleZ, 1f, 0, 0);
        return modelBuilder.end();
    }

    @Override
    public void dispose() {

    }
}
