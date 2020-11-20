package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.Image;

import static pl.greenmc.tob.TourOfBusiness.TOB;

class GamePlayers extends Scene implements Interactable {
    private int numPlayers = 8;
    private Image player1;
    private Image player2;
    private Image player3;
    private Image player4;
    private Image player5;
    private Image player6;
    private Image player7;
    private Image player8;

    @Override
    public void onMouseMove(int x, int y) {
    }

    @Override
    public void render() {
        float size = Math.min(Gdx.graphics.getHeight() / 6.0f / 3, Gdx.graphics.getWidth() / 3.0f / 4);
        float w = size * 4;
        float h = size * 3;
        if (numPlayers >= 1) player1.draw(0, Gdx.graphics.getHeight() - h, w, h);
        if (numPlayers >= 2) player2.draw(Gdx.graphics.getWidth() - w, Gdx.graphics.getHeight() - h, w, h);
        if (numPlayers >= 3) player3.draw(Gdx.graphics.getWidth() - w, 0, w, h);
        if (numPlayers >= 4) player4.draw(0, 0, w, h);
        if (numPlayers >= 5) player5.draw(0, Gdx.graphics.getHeight() - 2.1f * h, w, h);
        if (numPlayers >= 6) player6.draw(Gdx.graphics.getWidth() - w, Gdx.graphics.getHeight() - 2.1f * h, w, h);
        if (numPlayers >= 7) player7.draw(Gdx.graphics.getWidth() - w, 1.1f * h, w, h);
        if (numPlayers >= 8) player8.draw(0, 1.1f * h, w, h);
    }

    @Override
    public void setup() {
        player1 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player1.png"), Image.Align.STRETCH);
        player2 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player2.png"), Image.Align.STRETCH);
        player3 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player3.png"), Image.Align.STRETCH);
        player4 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player4.png"), Image.Align.STRETCH);
        player5 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player5.png"), Image.Align.STRETCH);
        player6 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player6.png"), Image.Align.STRETCH);
        player7 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player7.png"), Image.Align.STRETCH);
        player8 = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player8.png"), Image.Align.STRETCH);
        player1.setup();
        player2.setup();
        player3.setup();
        player4.setup();
        player5.setup();
        player6.setup();
        player7.setup();
        player8.setup();
    }

    @Override
    public void resize(int width, int height) {
        player1.resize(width, height);
        player2.resize(width, height);
        player3.resize(width, height);
        player4.resize(width, height);
        player5.resize(width, height);
        player6.resize(width, height);
        player7.resize(width, height);
        player8.resize(width, height);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        player1.dispose();
        player2.dispose();
        player3.dispose();
        player4.dispose();
        player5.dispose();
        player6.dispose();
        player7.dispose();
        player8.dispose();
    }
}
