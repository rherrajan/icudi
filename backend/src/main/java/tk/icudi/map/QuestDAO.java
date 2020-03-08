package tk.icudi.map;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class QuestDAO {

	@Autowired
	private DataSource dataSource;

	public void saveQuest(ResponseEntity<String> response, String uuid) {
		// System.out.println(" --- response: " + response);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jsonResponse = mapper.readTree(response.getBody());

//			System.out.println(" --- jsonResponse: " + jsonResponse);
			JsonNode hit = jsonResponse.get("query").get("geosearch").get(0);
			saveQuestInDB(hit, uuid);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createTable(Statement stmt) throws SQLException {
		String createQuery = "CREATE TABLE IF NOT EXISTS quest (x INT NOT NULL, y INT NOT NULL, uuid TEXT, hit TEXT, claimed BOOLEAN, time TIMESTAMP)";
//		System.out.println(" --- createQuery: " + createQuery);
		stmt.executeUpdate(createQuery);
	}

	private void saveQuestInDB(JsonNode hit, String uuid) {
		System.out.println(" --- new quest: " + hit.toString());
		String lat = hit.get("lat").asText();
		String lon = hit.get("lon").asText();
		Point location = new Point(lat, lon);

//		String title = hit.get("title").asText();
//		String type = hit.get("type").asText();
//		String pageid = hit.get("pageid").asText();

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			createTable(stmt);

			String insertQuery = "INSERT INTO quest VALUES (" + location.getX() + ", " + location.getY() + ", '" + uuid
					+ "', '" + hit.toString() + "',false,now())";
//			System.out.println(" --- insertQuery: " + insertQuery);
			stmt.executeUpdate(insertQuery);

//			return fetchQuests(stmt);
		} catch (Exception e) {
			e.printStackTrace();

//			StringWriter sw = new StringWriter();
//			e.printStackTrace(new PrintWriter(sw));
//			String stackTrace = sw.toString();
//			return "error: " + e + "\n" + stackTrace;
		}

	}

	public String setClaimed(Statement stmt, Point click) throws SQLException {

		createTable(stmt);

		String selectQuery = "SELECT * FROM quest WHERE x=" + click.getX() + " AND y=" + click.getY()
				+ " AND claimed=false";
//		System.out.println(" --- selectQuery: " + selectQuery);
		ResultSet rs = stmt.executeQuery(selectQuery);
		boolean hasItems = rs.next();
//		System.out.println(" --- hasItems: " + hasItems);
		if (!hasItems) {
			return "";
		}
		String foundItem = rs.getString("hit");
		System.out.println("  --- found: " + foundItem);

		String updateQuery = "UPDATE quest SET claimed=true WHERE x=" + click.getX() + " AND y=" + click.getY();
//		System.out.println(" --- updateQuery: " + updateQuery);
		stmt.executeUpdate(updateQuery);
		return foundItem;
	}

	@RequestMapping(value = "/selectQuests", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object getQuests() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			return getQuests(stmt);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	public List<Quest> getQuests(Statement stmt) throws SQLException {
		createTable(stmt);

		ResultSet rs = stmt.executeQuery("SELECT * FROM quest");
		List<Quest> quests = new ArrayList<Quest>();
		while (rs.next()) {
			quests.add(new Quest(rs.getInt("x"), rs.getInt("y"), rs.getString("uuid"), rs.getString("hit"),
					rs.getBoolean("claimed"), rs.getTimestamp("time")));
		}
		return quests;
	}

	public Object getHighscore() {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT uuid, COUNT (uuid) FROM quest WHERE claimed=true GROUP BY uuid ORDER BY count DESC");

			List<Object> score = new ArrayList<Object>();
			while (rs.next()) {
				score.add(Arrays.asList(rs.getString("uuid"), rs.getString("count")));
			}
			return score;

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	public Object getNearestHit(Double lat, Double lng, String uuid) {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			return getNearestHit(stmt, lat, lng, uuid);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();
			return "error: " + e + "\n" + stackTrace;
		}
	}

	private Object getNearestHit(Statement stmt, Double lat, Double lng, String uuid) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM quest WHERE claimed=false AND uuid='" + uuid + "'");

		List<Quest> quests = new ArrayList<Quest>();
		while (rs.next()) {
			quests.add(new Quest(rs.getInt("x"), rs.getInt("y"), rs.getString("uuid"), rs.getString("hit"),
					rs.getBoolean("claimed"), rs.getTimestamp("time")));
		}

		Point playerLocation = new Point(lat, lng);

		Optional<Hit> nearestHit = quests.stream().map(quest -> calculuateHit(quest, playerLocation)) //
				.sorted(Comparator.comparingDouble(Hit::getDistInMeter)).findFirst();


		return nearestHit.get();
	}

	public static class Hit {
		// {"pageid":7960945,"ns":0,"title":"St. Joseph
		// (Mainz)","lat":50.00982,"lon":8.2625,"dist":41,"primary":"","type":"landmark"}
		private String title;
		private Double lat;
		private Double lon;
		private double distInMeter;
		private Direction direction;

		public String getTitle() {
			return title;
		}

		public Double getLat() {
			return lat;
		}

		public Double getLon() {
			return lon;
		}

		public double getDistInMeter() {
			return distInMeter;
		}

		public Direction getDirection() {
			return direction;
		}
	}

	private Hit calculuateHit(Quest entry, Point playerLocation) {
		String hitString = entry.getHit();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			Hit hit = objectMapper.readValue(hitString, Hit.class);
			Point poi = new Point(hit.lat, hit.lon);
			hit.distInMeter = poi.distFrom(playerLocation);
			hit.direction = poi.getDirectionFrom(playerLocation);
			System.out.println(" --- enfernung zu '" + hit.title + "' : '" + hit.distInMeter + "' " + hit.direction);
			return hit;
		} catch (IOException e) {
			throw new RuntimeException("could not parse '" + hitString + "'", e);
		}
	}

	@RequestMapping(value = "/dropQuestDB", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object dropDB() throws IOException {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS quest");
			return "{\"result\":\"success\"}";
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

}
