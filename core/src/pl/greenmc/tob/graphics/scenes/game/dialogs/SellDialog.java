package pl.greenmc.tob.graphics.scenes.game.dialogs;

import com.badlogic.gdx.Gdx;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.Label;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class SellDialog extends Dialog {
    private final Label label;
    private final Button ok, autoSell;

    public SellDialog(long sellAmount, long currentAmount, @NotNull Runnable onOk, @NotNull Runnable onAutoSell) {
        super(new HSplitPane(), Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 2f);
        HSplitPane pane = (HSplitPane) getChild();

        ok = new Button("Gotowe");
        autoSell = new Button("Sprzedaj automatycznie");
        if (currentAmount < sellAmount)
            ok.applyDisabledTheme();
        else
            ok.applyYesTheme();

        ok.setClickCallback(onOk);
        autoSell.setClickCallback(onAutoSell);

        label = new Label("Potrzebne pieniądze: " + Utilities.makeMoney(sellAmount) + "\nPieniądze po sprzedaży: " + Utilities.makeMoney(currentAmount), (int) (TOB.getFontBase() / 6), false);
        pane.addChild(ok, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(autoSell, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        pane.addChild(label, new HSplitPane.ElementOptions(250, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        ok.setFontSize((int) (TOB.getFontBase() / 6));
        autoSell.setFontSize((int) (TOB.getFontBase() / 6));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        setWidth(Gdx.graphics.getWidth() / 5f);
        setHeight(Gdx.graphics.getHeight() / 2f);
        label.setFontSize((int) (TOB.getFontBase() / 6));
        ok.setFontSize((int) (TOB.getFontBase() / 6));
        autoSell.setFontSize((int) (TOB.getFontBase() / 6));
    }
}
