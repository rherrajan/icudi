package tk.icudi.map;

public enum Direction {

	N("Norden"),
	NE("Nordosten"),
	E("Osten"),
	SE("Südosten"),
	S("Süden"),
	SW("Südwesten"),
	W("Westen"), 
	NW("Nordwesten");

	private String name;

	private Direction(String name) {
		this.name = name;
	}
	
	public static Direction valueOfAngle(double angle) {
		int index = (int) Math.round((((double) angle % 360) / 45));
		if (index == Direction.values().length) {
			index = 0;
		}
		return Direction.values()[index];
	}

	public String getName() {
		return name;
	}

}
