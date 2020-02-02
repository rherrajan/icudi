package tk.icudi.map;

public class Cell {

	private int x;
	private int y;
	private String owner;

	public Cell(int x, int y, String owner) {
		this.x = x;
		this.y = y;
		this.owner = owner;
	}

	@Override
	public String toString() {
		return x + "/" + y + " => " + owner;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getOwner() {
		return owner;
	}
	
}
