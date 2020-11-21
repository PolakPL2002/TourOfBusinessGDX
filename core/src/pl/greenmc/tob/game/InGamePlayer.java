package pl.greenmc.tob.game;

public class InGamePlayer extends Player {
    private final int balance;

    public InGamePlayer(int ID, String identity, String name, int balance) {
        super(ID, identity, name);
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }
}
