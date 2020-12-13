package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.Label;
import pl.greenmc.tob.graphics.elements.VSplitPane;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import java.util.Timer;
import java.util.TimerTask;

public class YesNoDialog extends Dialog {
    private long period = 0;
    private Runnable routine = null;
    private Timer timer = null;

    public YesNoDialog(@NotNull String query, @NotNull Runnable onYes, @NotNull Runnable onNo, @NotNull Runnable routine, long period) {
        this(query, onYes, onNo);

        this.routine = routine;
        this.period = period;
    }

    public YesNoDialog(@NotNull String query, @NotNull Runnable onYes, @NotNull Runnable onNo) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        HSplitPane pane = (HSplitPane) getChild();

        Button yes = new Button("Tak"), no = new Button("Nie");

        yes.applyYesTheme();
        no.applyNoTheme();

        yes.setClickCallback(onYes);
        no.setClickCallback(onNo);

        VSplitPane buttons = new VSplitPane()
                .addChild(yes, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE))
                .addChild(no, new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE));

        Label label = new Label(query, 24, true);
        label.setBackgroundColor(new Color(1, 1, 1, .75f));
        pane.addChild(buttons, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED))
                .addChild(label, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void setup() {
        super.setup();
        if (routine != null && period != 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    routine.run();
                }
            }, 0, period);
        }
    }
}
