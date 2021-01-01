package pl.greenmc.tob.game.map;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CardDeck {
    private final ArrayList<Card> cards = new ArrayList<>();

    @NotNull
    public Card getRandomCard() {
        return cards.get((int) (Math.random() * cards.size()));
    }

    public CardDeck addCard(@NotNull Card card) {
        cards.add(card);
        return this;
    }

    public Card[] getCards() {
        return cards.toArray(new Card[0]);
    }
}
