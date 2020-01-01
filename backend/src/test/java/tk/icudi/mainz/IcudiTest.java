package tk.icudi.mainz;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class IcudiTest {

	private Player player;
	private SmallBoard board;

	@Before
	public void setupGameboard() {
		player = new Player();
		board = new SmallBoard();
		board.startOccupation(50.007529, 8.266509);
	}
	
	@Test
	public void shouldStartGameboard() {
		assertThat(board.getTick(), is(0));
		assertThat(player.getCardCount(Card.FARM), is(2));
	}
	
	@Test
	public void shouldPlayCards() {	
		board.play(Card.FARM, player, 0, 0);
		assertThat(board.getTick(), is(1));
		assertThat(player.getCardCount(Card.FARM), is(1));
	}
	
	@Test
	public void shouldHarvest() {
		board.play(Card.FARM, player, 0, 0);
		Card retrieved = board.harvest(player, 0, 0);
		assertThat(retrieved, anyOf(is(Card.FARM), is(Card.VILLAGE)));
		assertThat(board.getTick(), is(2));
	}
		
	// no villages without 2 Farms
	// multplie small boards
	
	// no duplicate harvests?
	// barracks
	// moster attacks
	

}
