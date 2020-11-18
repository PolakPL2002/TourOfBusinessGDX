package pl.greenmc.tob.graphics;

public interface Interactable {
    default void onMouseDown() {
    }

    default void onMouseEnter() {
    }

    default void onMouseLeave() {
    }

    default void onMouseMove(int x, int y) {
    }

    default void onMouseUp() {
    }
}
