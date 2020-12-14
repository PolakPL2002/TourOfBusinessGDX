package pl.greenmc.tob.graphics.scenes.game.dialogs;

import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.game.util.Utilities;
import pl.greenmc.tob.graphics.elements.Button;
import pl.greenmc.tob.graphics.elements.HSplitPane;
import pl.greenmc.tob.graphics.elements.Label;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

public class SellDialog extends Dialog {
    public SellDialog(long sellAmount, long currentAmount, @NotNull Runnable onOk, @NotNull Runnable onAutoSell) {
        super(new HSplitPane(), 250, 350);
        HSplitPane pane = (HSplitPane) getChild();
        Button ok = new Button("Gotowe"), autoSell = new Button("Sprzedaj automatycznie");
        if (currentAmount < sellAmount)
            ok.applyDisabledTheme();
        else
            ok.applyYesTheme();

        ok.setClickCallback(onOk);
        autoSell.setClickCallback(onAutoSell);

        Label label = new Label("Potrzebne pieniądze: " + Utilities.makeMoney(sellAmount) + "\nPieniądze po sprzedaży: " + Utilities.makeMoney(currentAmount), 18, false);
        pane.addChild(ok, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        pane.addChild(autoSell, new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED));
        pane.addChild(label, new HSplitPane.ElementOptions(250, HSplitPane.ElementOptions.HeightMode.FIXED));
    }
}
