package tk.icudi.map;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

	public boolean setClaimed(Statement stmt, Point click) throws SQLException {

		createTable(stmt);
		
		String selectQuery = "SELECT * FROM quest WHERE x=" + click.getX() +" AND y=" + click.getY() + " AND claimed=false";
//		System.out.println(" --- selectQuery: " + selectQuery);
		ResultSet rs = stmt.executeQuery(selectQuery);
		boolean hasItems = rs.next();
//		System.out.println(" --- hasItems: " + hasItems);
		if(!hasItems) {
			return false;
		}
		System.out.println("  --- found: " + rs.getString("hit"));
		
		String updateQuery = "UPDATE quest SET claimed=true WHERE x=" + click.getX() +" AND y=" + click.getY();
//		System.out.println(" --- updateQuery: " + updateQuery);
		stmt.executeUpdate(updateQuery);	    
		return true;
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
