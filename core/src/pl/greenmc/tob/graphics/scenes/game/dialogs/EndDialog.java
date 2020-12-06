package pl.greenmc.tob.graphics.scenes.game.dialogs;

import pl.greenmc.tob.graphics.elements.Label;
import pl.greenmc.tob.graphics.scenes.game.Dialog;

public class EndDialog extends Dialog {
    public EndDialog() {
        super(new Label("End", 24, false), 100, 100);
    }
}
