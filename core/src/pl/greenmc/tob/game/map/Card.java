package pl.greenmc.tob.game.map;

public class Card {
    private final String name;
    private final CardType type;
    private final long value;

    public Card(String name, CardType type) {
        this(name, type, 0);
    }

    public Card(String name, CardType type, long value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }

    public CardType getType() {
        return type;
    }

    public enum CardType {
        GET_OUT_OF_JAIL,
        MODIFIED_RENT
    }
}
