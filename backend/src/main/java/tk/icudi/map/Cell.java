package tk.icudi.map;

public class Cell {

	private int x;
	private int y;
	private String owner;
	private String value;

	public Cell(int x, int y, String owner, String value) {
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.value = value;
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

	public String getValue() {
		return value;
	}
	
}
