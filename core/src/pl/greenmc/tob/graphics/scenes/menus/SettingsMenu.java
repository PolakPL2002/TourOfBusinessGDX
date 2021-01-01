package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.graphics.Color;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class SettingsMenu extends Menu {

    private Button button1;
    private PaddingPane menuPadding;
    private PaddingPane padding1;

    @Override
    public void setup() {
        super.setup();
        button1 = new Button("Wróć");

        button1.setClickCallback(this::onBack);

        HSplitPane menu = new HSplitPane()
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(226, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                );
        menu.setBackgroundColor(new Color(0, 0, 0, 0));
        menuPadding = new PaddingPane(
                menu,
                TOB.getFontBase() / 12
        );
        menuPadding.setColor(GlobalTheme.menuBackgroundColor);
        setElement(
                new VSplitPane()
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new HSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(210, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                padding1 = new PaddingPane(
                                                        menuPadding,
                                                        TOB.getFontBase() / 40
                                                ),
                                                new HSplitPane.ElementOptions(300, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(210, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
        updateSizes();
    }

    private void onBack() {
        TOB.runOnGLThread(() -> TOB.changeScene(new MainMenu()));
    }

    private void updateSizes() {
        if (button1 != null) button1.setFontSize((int) (TOB.getFontBase() / 6));
        if (padding1 != null) padding1.setPadding(TOB.getFontBase() / 40);
        if (menuPadding != null) menuPadding.setPadding(TOB.getFontBase() / 12);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateSizes();
    }
}
