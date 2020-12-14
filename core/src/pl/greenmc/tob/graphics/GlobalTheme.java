package pl.greenmc.tob.graphics;


import com.badlogic.gdx.graphics.Color;

public class GlobalTheme {
    public static final Color buttonDisabledBackgroundColor = new Color(0.7f, 0.7f, 0.7f, 1f);
    public static final Color buttonDisabledBorderColor = new Color(0.3f, 0.3f, 0.3f, 1f);
    public static final Color buttonDisabledClickColor = buttonDisabledBackgroundColor;
    public static final Color buttonDisabledHoverColor = buttonDisabledBackgroundColor;
    public static final Color buttonNoBackgroundColor = MaterialColor.RED.color200();
    public static final Color buttonNoBorderColor = MaterialColor.RED.color700();
    public static final Color buttonNoClickColor = MaterialColor.RED.color400();
    public static final Color buttonNoHoverColor = MaterialColor.RED.color300();
    public static final Color buttonYesBackgroundColor = MaterialColor.GREEN.color200();
    public static final Color buttonYesBorderColor = MaterialColor.GREEN.color700();
    public static final Color buttonYesClickColor = MaterialColor.GREEN.color400();
    public static final Color buttonYesHoverColor = MaterialColor.GREEN.color300();
    public static final Color errorBackgroundColor = MaterialColor.RED.color200();
    public static final Color[] playerColors = new Color[]{
            MaterialColor.RED.color500(),
            MaterialColor.BLUE.color500(),
            MaterialColor.YELLOW.color500(),
            MaterialColor.GREEN.color500(),
            MaterialColor.PURPLE.color500(),
            MaterialColor.ORANGE.color500(),
            MaterialColor.TEAL.color300(),
            MaterialColor.PINK.color300()};
    public static final MaterialColor scheme = MaterialColor.BLUE;
    public static final Color barBackgroundColor = scheme.color300();
    public static final Color barHandleColor = scheme.color700();
    public static final Color backgroundColor = scheme.color100();
    public static final Color buttonBackgroundColor = scheme.color200();
    public static final Color buttonBorderColor = scheme.color700();
    public static final Color buttonClickColor = scheme.color400();
    public static final Color buttonHoverColor = scheme.color300();
    public static final Color menuBackgroundColor = scheme.color50();
    public static final Color progressBarBackgroundColor = scheme.color100();
    public static final Color progressBarBorderColor = scheme.color700();
    public static final Color progressBarColor = scheme.color500();
    public static final Color textColor = Color.BLACK;
}
