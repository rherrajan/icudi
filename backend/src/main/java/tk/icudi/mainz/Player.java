package tk.icudi.mainz;

import java.util.HashMap;

public class Player {

	private HashMap<Card, Integer> cardMap;

	public Player() {
		this.cardMap = new HashMap<Card, Integer>();
		cardMap.put(Card.FARM, 2);
		cardMap.put(Card.VILLAGE, 1);
	}
	
	public int getCardCount(Card card) {
		return cardMap.get(card);
	}

	public void decreaseCard(Card card) {
		int count = cardMap.get(card);
		cardMap.put(card, count-1);
	}

	public void increaseCard(Card card) {
		int count = cardMap.get(card);
		cardMap.put(card, count+1);
	}

}
