package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.graphics.Color;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import static pl.greenmc.tob.TourOfBusiness.TOB;

public class JoinGameMenu extends Menu {
    @Override
    public void setup() {
        Button button1 = new Button("Wróć");
        button1.setFontSize(20);

        button1.setClickCallback(this::onBack);

        VScrollPane element = new VScrollPane();
        element.addChild(new Button("Pokój 5"), 120)
                .addChild(new Button("Pokój 4"), 120)
                .addChild(new Button("Pokój 3"), 120)
                .addChild(new Button("Pokój 2"), 120)
                .addChild(new Button("Pokój 1"), 120);

        HSplitPane menu = new HSplitPane()
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                ).addChild(element, new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE));
        menu.setBackgroundColor(new Color(0, 0, 0, 0));
        PaddingPane menuPadding = new PaddingPane(
                menu,
                10
        );
        menuPadding.setColor(GlobalTheme.menuBackgroundColor);
        setElement(
                new VSplitPane()
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new HSplitPane()
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new PaddingPane(
                                                        menuPadding,
                                                        3
                                                ),
                                                new HSplitPane.ElementOptions(8, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
    }

    private void onBack() {
        TOB.runOnGLThread(() -> TOB.changeScene(new GameMenu()));
    }
}
