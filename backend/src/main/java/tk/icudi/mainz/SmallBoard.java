package tk.icudi.mainz;

public class SmallBoard {

	private int tick;
	
	private Tile[][] tiles = new Tile[5][5];

	public void startOccupation(double lat, double lng) {
		tick = 0;
	}

	public void play(Card card, Player player, int x, int y) {
		
		player.decreaseCard(card);

		Tile newTile = new Tile();
		newTile.setCard(card);
		newTile.setPlayer(player);
		tiles[x][y] = newTile;
		
		nextTick();
	}

	public Card harvest(Player player, int x, int y) {
		Tile harvested = tiles[x][y];
		if(harvested != null) {
			Card receivedCard = harvested.getCard().harvest();
			player.increaseCard(receivedCard);
			nextTick();
			return receivedCard;
		}
		
		return null;
	}

	public int getTick() {
		return tick;
	}

	private void nextTick() {
		tick++;
	}
	
}
