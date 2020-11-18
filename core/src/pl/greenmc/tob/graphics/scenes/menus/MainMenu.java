package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import static pl.greenmc.tob.TourOfBusiness.TOB;


public class MainMenu extends Menu {
    @Override
    public void setup() {
        Button button1 = new Button("Graj");
        Button button2 = new Button("Profil");
        Button button3 = new Button("Opcje");
        Button button4 = new Button("Wyjdź");
        button1.setFontSize(20);
        button2.setFontSize(20);
        button3.setFontSize(20);
        button4.setFontSize(20);

        button4.setBackgroundColor(GlobalTheme.buttonNoBackgroundColor);
        button4.setClickColor(GlobalTheme.buttonNoClickColor);
        button4.setHoverColor(GlobalTheme.buttonNoHoverColor);
        button4.setBorderColor(GlobalTheme.buttonNoBorderColor);

        button4.setClickCallback(() -> Gdx.app.exit());
        ((Music) TOB.getGame().getAssetManager().get("music/music1.wav")).play();
        HSplitPane menu = new HSplitPane()
                .addChild(
                        button4,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        button3,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(10, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        button2,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(10, HSplitPane.ElementOptions.HeightMode.FIXED)
                )
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.FIXED)
                );
        menu.setBackgroundColor(new Color(0, 0, 0, 0));
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
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        )
                                        .addChild(
                                                new PaddingPane(
                                                        menu,
                                                        3
                                                ),
                                                new HSplitPane.ElementOptions(300, HSplitPane.ElementOptions.HeightMode.FIXED)
                                        )
                                        .addChild(
                                                new TransparentColor(),
                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
    }
}
