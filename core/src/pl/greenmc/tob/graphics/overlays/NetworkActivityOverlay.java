package pl.greenmc.tob.graphics.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pl.greenmc.tob.game.netty.client.NettyClient;
import pl.greenmc.tob.graphics.Overlay;

public class NetworkActivityOverlay extends Overlay {
    private final int period;
    private SpriteBatch batch;
    private Texture[] textures;

    public NetworkActivityOverlay(int period) {
        this.period = period;
    }

    @Override
    public void setup() {
        batch = new SpriteBatch();
        textures = new Texture[4];
        textures[0] = new Texture(Gdx.files.internal("textures/ui/network/net4.png"), true);
        textures[1] = new Texture(Gdx.files.internal("textures/ui/network/net3.png"), true);
        textures[2] = new Texture(Gdx.files.internal("textures/ui/network/net2.png"), true);
        textures[3] = new Texture(Gdx.files.internal("textures/ui/network/net1.png"), true);
    }

    @Override
    public void draw() {
        if (NettyClient.getInstance().getClientHandler() != null && NettyClient.getInstance().getClientHandler().isTransmitting()) {
            int id = (int) (System.currentTimeMillis() % period / (period / textures.length));
            batch.begin();
            float size = Math.min(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
            batch.draw(textures[id], Gdx.graphics.getWidth() - size * 1.2f, Gdx.graphics.getHeight() - size * 1.2f, size, size);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        batch.dispose();
        batch = new SpriteBatch();
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        batch.dispose();
    }
}
