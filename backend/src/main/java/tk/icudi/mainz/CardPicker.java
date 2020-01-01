package tk.icudi.mainz;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

public class CardPicker {

    private static Random generator = new Random(System.currentTimeMillis());
	
	public static Card pickFrom(Map<Card, Integer> cardPossibilities) {
		
		System.out.println(" --- cardPossibilities: " + cardPossibilities);
		if(cardPossibilities.isEmpty()) {
			return null;
		}
		
		long max = cardPossibilities.values().stream().collect(Collectors.counting());
		
		System.out.println(" --- max: " + max);
		
		int hit = generateNumberBetween(0, (int)max);

		int current = -1;
		for (Entry<Card, Integer> entry : cardPossibilities.entrySet()) {
			current += entry.getValue();
			System.out.println("  --- current: " + current);
			if(current == hit) {
				return entry.getKey();
			}
		}
		
		throw new RuntimeException("error while caluculating card drow. max=" + max + ", current=" + current + ", cardPossibilities=" + cardPossibilities);
	}

	/** low is inclusive. high is exclusive */
	private static int generateNumberBetween(int low, int high) {
		return generator.nextInt(high-low) + low;
	}
	

}
