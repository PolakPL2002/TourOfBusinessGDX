package pl.greenmc.tob.game;

public class Player {
    private final int money;
    private final String name;

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public String getName() {
        return name;
    }
}
