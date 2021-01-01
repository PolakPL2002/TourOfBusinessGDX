package pl.greenmc.tob.graphics.scenes.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import pl.greenmc.tob.graphics.GlobalTheme;
import pl.greenmc.tob.graphics.elements.*;
import pl.greenmc.tob.graphics.scenes.Menu;

import static pl.greenmc.tob.TourOfBusiness.TOB;


public class MainMenu extends Menu {
    private final String error;
    private Button button1 = null;
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;
    private Label errorLabel;
    private PaddingPane menuPadding;
    private PaddingPane padding1 = null;

    public MainMenu() {
        error = "";
    }

    public MainMenu(@NotNull String error) {
        this.error = error;
    }

    @Override
    public void setup() {
        super.setup();
        button1 = new Button("Graj");
        button2 = new Button("Profil");
        button3 = new Button("Opcje");
        button4 = new Button("Wyjdź");

        updateSizes();

        button4.applyNoTheme();

        button1.setClickCallback(this::onPlay);
        button2.setClickCallback(this::onProfile);
        button3.setClickCallback(this::onSettings);
        button4.setClickCallback(() -> Gdx.app.exit());

        HSplitPane menu = new HSplitPane()
                .addChild(
                        button4,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(54, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        button3,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(10, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        button2,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        new TransparentColor(),
                        new HSplitPane.ElementOptions(10, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                )
                .addChild(
                        button1,
                        new HSplitPane.ElementOptions(50, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                );
        menu.setBackgroundColor(new Color(0, 0, 0, 0));
        menuPadding = new PaddingPane(
                menu,
                TOB.getFontBase() / 12
        );
        menuPadding.setColor(GlobalTheme.menuBackgroundColor);
        errorLabel = new Label(error, (int) (TOB.getFontBase() / 7), false);
        if (error.length() > 0)
            errorLabel.setBackgroundColor(new Color(1, 1, 1, 0.75f));
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
                                                new HSplitPane()
                                                        .addChild(
                                                                errorLabel,
                                                                new HSplitPane.ElementOptions(2, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                                        )
                                                        .addChild(
                                                                new TransparentColor(),
                                                                new HSplitPane.ElementOptions(1, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                                        ),
                                                new HSplitPane.ElementOptions(210, HSplitPane.ElementOptions.HeightMode.VARIABLE)
                                        ),
                                new VSplitPane.ElementOptions(1, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
                        .addChild(
                                new TransparentColor(),
                                new VSplitPane.ElementOptions(2, VSplitPane.ElementOptions.WidthMode.VARIABLE)
                        )
        );
    }

    private void onPlay() {
        TOB.runOnGLThread(() -> TOB.changeScene(new GameMenu()));
    }

    private void onProfile() {
        TOB.runOnGLThread(() -> TOB.changeScene(new ProfileMenu()));
    }

    private void onSettings() {
        TOB.runOnGLThread(() -> TOB.changeScene(new SettingsMenu()));
    }

    private void updateSizes() {
        if (button1 != null) button1.setFontSize((int) (TOB.getFontBase() / 6));
        if (button2 != null) button2.setFontSize((int) (TOB.getFontBase() / 6));
        if (button3 != null) button3.setFontSize((int) (TOB.getFontBase() / 6));
        if (button4 != null) button4.setFontSize((int) (TOB.getFontBase() / 6));
        if (padding1 != null) padding1.setPadding(TOB.getFontBase() / 40);
        if (menuPadding != null) menuPadding.setPadding(TOB.getFontBase() / 12);
        if (errorLabel != null) errorLabel.setFontSize((int) (TOB.getFontBase() / 7));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateSizes();
    }
}
