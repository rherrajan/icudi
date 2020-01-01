package tk.icudi.mainz;

import java.util.HashMap;
import java.util.Map;

public enum Card {

	FARM {
		@Override
		public Card harvest() {
			Map<Card, Integer> cardPossibilities = new HashMap<Card, Integer>();
			cardPossibilities.put(FARM, 1);
			cardPossibilities.put(VILLAGE, 1);
			return CardPicker.pickFrom(cardPossibilities);
		}

	},
	VILLAGE {
		@Override
		public Card harvest() {
			Map<Card, Integer> cardPossibilities = new HashMap<Card, Integer>();
			cardPossibilities.put(FARM, 1);
			cardPossibilities.put(VILLAGE, 1);
			return CardPicker.pickFrom(cardPossibilities);
		}
	};

	public abstract Card harvest();

}
