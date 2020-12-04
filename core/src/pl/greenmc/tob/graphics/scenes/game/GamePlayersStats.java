package pl.greenmc.tob.graphics.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.Interactable;
import pl.greenmc.tob.graphics.Scene;
import pl.greenmc.tob.graphics.elements.Image;
import pl.greenmc.tob.graphics.elements.ProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.badlogic.gdx.graphics.GL20.*;
import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.game.util.Utilities.LATIN_EXTENDED;

class GamePlayersStats extends Scene implements Interactable {
    private final int PLAYER_SLOTS = 8;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Regular.ttf"));
    private final FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans-Bold.ttf"));
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private SpriteBatch batch;
    private BitmapFont fontMoney;
    private BitmapFont fontNames;
    private Image[] images = new Image[PLAYER_SLOTS];
    private GlyphLayout layout;
    private PlayerInfo[] players = new PlayerInfo[0];
    private Rectangle[] positions = new Rectangle[PLAYER_SLOTS];
    private ProgressBar progressBar;
    private long timeoutEnd = 0;
    private int timeoutTotal = 0;
    private int timerOnPlayer = -1;

    @Override
    public void onMouseMove(int x, int y) {
    }

    public void setPlayerName(int player, String name) {
        players[player % players.length].name = name;
    }

    public void setPlayerBalance(int player, long balance) {
        players[player % players.length].balance = balance;
    }

    @Override
    public void render() {
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        for (int i = 0; i < Math.min(players.length, PLAYER_SLOTS); i++) {
            drawStats(positions[i], players[i], images[i], i % 4 == 1 || i % 4 == 2, i == timerOnPlayer);
        }
        batch.end();
    }

    private void drawStats(@NotNull Rectangle pos, @NotNull PlayerInfo player, @NotNull Image image, boolean alignRight, boolean drawTimer) {
        image.draw(pos.x, pos.y, pos.width, pos.height);
        if (alignRight) {
            layout.setText(fontNames, player.getName());
            fontNames.draw(batch, player.getName(), pos.x + pos.width - pos.height / 3 / 5 - layout.width, pos.y + pos.height - pos.height / 3 / 4);
            layout.setText(fontMoney, player.getBalance() + "$");
            fontMoney.draw(batch, player.getBalance() + "$", pos.x + pos.width - pos.height / 3 / 5 - layout.width, pos.y + pos.height - 2 * pos.height / 3 / 4 - pos.height / 7);
        } else {
            fontNames.draw(batch, player.getName(), pos.x + pos.height / 3 / 5, pos.y + pos.height - pos.height / 3 / 4);
            fontMoney.draw(batch, player.getBalance() + "$", pos.x + pos.height / 3 / 5, pos.y + pos.height - 2 * pos.height / 3 / 4 - pos.height / 7);
        }
        if (drawTimer && timeoutTotal > 0) {
            progressBar.setMax(timeoutTotal);
            progressBar.setValue(timeoutEnd - System.currentTimeMillis());
            float left = (timeoutEnd - System.currentTimeMillis()) / 1000f;
            if (left < 0) left = 0;
            progressBar.setText(decimalFormat.format(left) + "/" + decimalFormat.format(timeoutTotal / 1000f) + "s");
            progressBar.draw(pos.x + pos.width * 0.1f, pos.y + pos.height * 0.45f, pos.width * 0.8f, pos.height * 0.1f);
        }
    }

    public void setTimeout(long timeoutLeft, int timeoutTotal) {
        timeoutEnd = System.currentTimeMillis() + timeoutLeft - 150;
        this.timeoutTotal = timeoutTotal;
    }

    public void setCurrentPlayer(int turnOf) {
        timerOnPlayer = turnOf;
    }

    public void setNumPlayers(int num) {
        PlayerInfo[] oldPI = players;
        players = new PlayerInfo[num];
        if (Math.min(oldPI.length, players.length) >= 0)
            System.arraycopy(oldPI, 0, players, 0, Math.min(oldPI.length, players.length));
        for (int i = Math.min(oldPI.length, players.length); i < players.length; i++)
            players[i] = new PlayerInfo("", 0);
    }

    @Override
    public void setup() {
        for (int i = 0; i < PLAYER_SLOTS; i++) {
            images[i] = new Image(TOB.getGame().getAssetManager().get("textures/ui/game/player" + (i + 1) + ".png"), Image.Align.STRETCH);
            images[i].setup();
        }
        float size = Math.min(Gdx.graphics.getHeight() / 6.0f / 3, Gdx.graphics.getWidth() / 3.0f / 4);
        float h = size * 3;
        batch = new SpriteBatch();
        setMoneyFontSize((int) (h / 9));
        setNamesFontSize((int) (h / 7));
        updatePositions();
        progressBar = new ProgressBar();
        progressBar.setup();
        progressBar.setFontSize((int) (h * 0.08f));
        progressBar.setBackgroundColor(new Color(0, 0, 0, 0));
        progressBar.setBorderColor(Color.BLACK);
        progressBar.setTextMode(ProgressBar.TextMode.CUSTOM);
        progressBar.setValue(50);
    }

    public void setMoneyFontSize(int size) {
        parameter.size = size;
        parameter.characters = LATIN_EXTENDED;
        if (fontMoney != null) fontMoney.dispose();
        fontMoney = generator.generateFont(parameter);
        layout = new GlyphLayout(fontMoney, "");
    }

    public void setNamesFontSize(int size) {
        parameter.size = size;
        parameter.characters = LATIN_EXTENDED;
        if (fontNames != null) fontNames.dispose();
        fontNames = generator2.generateFont(parameter);
        layout = new GlyphLayout(fontNames, "");
    }

    private void updatePositions() {
        float size = Math.min(Gdx.graphics.getHeight() / 6.0f / 3, Gdx.graphics.getWidth() / 3.0f / 4);
        float w = size * 4;
        float h = size * 3;
        positions[0] = new Rectangle(0, Gdx.graphics.getHeight() - h, w, h);
        positions[1] = new Rectangle(Gdx.graphics.getWidth() - w, Gdx.graphics.getHeight() - h, w, h);
        positions[2] = new Rectangle(Gdx.graphics.getWidth() - w, 0, w, h);
        positions[3] = new Rectangle(0, 0, w, h);
        positions[4] = new Rectangle(0, Gdx.graphics.getHeight() - 2.1f * h, w, h);
        positions[5] = new Rectangle(Gdx.graphics.getWidth() - w, Gdx.graphics.getHeight() - 2.1f * h, w, h);
        positions[6] = new Rectangle(Gdx.graphics.getWidth() - w, 1.1f * h, w, h);
        positions[7] = new Rectangle(0, 1.1f * h, w, h);
    }

    @Override
    public void resize(int width, int height) {
        for (int i = 0; i < PLAYER_SLOTS; i++)
            images[i].resize(width, height);

        if (batch != null) batch.dispose();

        float size = Math.min(Gdx.graphics.getHeight() / 6.0f / 3, Gdx.graphics.getWidth() / 3.0f / 4);
        float h = size * 3;
        batch = new SpriteBatch();
        setMoneyFontSize((int) (h / 9));
        setNamesFontSize((int) (h / 7));
        updatePositions();
        progressBar.resize(width, height);
        progressBar.setFontSize((int) (h * 0.08f));
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        for (int i = 0; i < PLAYER_SLOTS; i++)
            images[i].dispose();
        generator.dispose();
        generator2.dispose();
        if (batch != null) batch.dispose();
        fontMoney.dispose();
        progressBar.dispose();
    }

    private static class PlayerInfo {
        private long balance;
        private String name;

        public PlayerInfo(String name, long balance) {
            this.name = name;
            this.balance = balance;
        }

        public String getName() {
            return name;
        }

        public long getBalance() {
            return balance;
        }
    }
}
