package tk.icudi.map;

import java.sql.Timestamp;

public class Quest {

	private int x;
	private int y;
	private String uuid;
	private String hit;
	private boolean claimed;
	private Timestamp time;

	public Quest(int x, int y, String uuid, String hit, boolean claimed, Timestamp time) {
		this.x = x;
		this.y = y;
		this.uuid = uuid;
		this.hit = hit;
		this.claimed = claimed;
		this.time = time;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getUuid() {
		return uuid;
	}

	public String getHit() {
		return hit;
	}

	public boolean isClaimed() {
		return claimed;
	}

	public Timestamp getTime() {
		return time;
	}

}
