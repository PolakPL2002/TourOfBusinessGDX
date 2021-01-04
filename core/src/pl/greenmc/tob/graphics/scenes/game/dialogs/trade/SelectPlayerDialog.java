package pl.greenmc.tob.graphics.scenes.game.dialogs.trade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static pl.greenmc.tob.TourOfBusiness.TOB;
import static pl.greenmc.tob.graphics.scenes.game.GameScene.onEndAction;

public class SelectPlayerDialog extends Dialog {
    private final Button back;
    private final Label label;
    @NotNull
    private final HashMap<String, Integer> names;
    @NotNull
    private final SelectCallback onSelect;
    private final VSplitPane topRow, bottomRow;
    private Timer timer;

    public SelectPlayerDialog(@NotNull HashMap<String, Integer> names, @NotNull Runnable onBack, @NotNull SelectCallback onSelect) {
        super(new HSplitPane(), Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() / 2f);

        setSize();

        this.names = names;
        this.onSelect = onSelect;
        HSplitPane pane = (HSplitPane) getChild();

        back = new Button("Wróć");
        back.setClickCallback(onBack);
        pane.addChild(back, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        topRow = new VSplitPane();
        bottomRow = new VSplitPane();
        pane.addChild(bottomRow, new HSplitPane.ElementOptions(155, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(topRow, new HSplitPane.ElementOptions(155, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.addChild(label = new Label("Wybierz gracza", (int) (TOB.getFontBase() / 6), false), new HSplitPane.ElementOptions(30, HSplitPane.ElementOptions.HeightMode.VARIABLE));

        pane.setDrawBackground(true);
        pane.setBackgroundColor(GlobalTheme.backgroundColor);

        back.setFontSize((int) (TOB.getFontBase() / 6));
        generate();
    }

    private void setSize() {
        if (Gdx.graphics.getWidth() / Gdx.graphics.getHeight() > 62 / 39) {
            setHeight(Gdx.graphics.getHeight() * 0.8f);
            setWidth(Gdx.graphics.getHeight() * 0.8f * 62 / 39);
        } else {
            setWidth(Gdx.graphics.getWidth() * 0.8f);
            setHeight(Gdx.graphics.getWidth() * 0.8f * 39 / 62);
        }
    }

    private void generate() {
        topRow.clearChildren();
        bottomRow.clearChildren();
        Object[] n = names.keySet().toArray();
        for (int i = 0; i < names.keySet().size(); i++) {
            String s = (String) n[i];
            Button button = new Button(s);
            button.setBorderColor(Color.BLACK);
            button.setBackgroundColor(GlobalTheme.playerColors[i]);
            Color hoverColor = GlobalTheme.playerColors[i].cpy().mul(0.9f);
            hoverColor.a = 1;
            button.setHoverColor(hoverColor);
            Color clickColor = GlobalTheme.playerColors[i].cpy().mul(0.8f);
            clickColor.a = 1;
            button.setClickColor(clickColor);
            button.setClickCallback(() -> onSelect.run(names.get(s)));
            button.setFontSize((int) (TOB.getFontBase() / 6));
            (i < 4 ? topRow : bottomRow).addChild(button, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        }
        for (int i = names.keySet().size(); i < 8; i++) {
            (i < 4 ? topRow : bottomRow).addChild(new TransparentColor(), new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));
        }
    }

    @Override
    public void setup() {
        super.setup();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onEndAction();
            }
        }, 0, 3000);
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (timer != null) timer.cancel();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setSize();
        back.setFontSize((int) (TOB.getFontBase() / 6));
        label.setFontSize((int) (TOB.getFontBase() / 6));
        generate();
    }

    public interface SelectCallback {
        void run(int playerID);
    }
}
