package pl.greenmc.tob.game;

//TODO
public class Player {
    private final int ID;
    private final String identity;
    private final String name;

    public Player(int ID, String identity, String name) {
        this.ID = ID;
        this.identity = identity;
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public String getIdentity() {
        return identity;
    }

    public String getName() {
        return name;
    }
}
