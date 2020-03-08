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
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ClickDetecter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private QuestDAO questDAO;

	class Response {
		public String error;
		public Object cells;
		public boolean success;
		public Object foundItem;
	}
	
	@RequestMapping(value = "/click", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Response onClick(@RequestParam Double lat, @RequestParam Double lng, @RequestParam String uuid) throws IOException {
		Point click = new Point(lat, lng);
		saveClick(click, uuid);

		Response response = new Response();
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			changeOwner(click, uuid, stmt, response); 
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();
			response.error = "error: " + e + "\n" + stackTrace;
		}
		
		return response;
	}

	@RequestMapping(value = "/getCells", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object getCells(@RequestParam Double lat, @RequestParam Double lng) throws IOException {
		return fetchCells(lat, lng);
	}

	@RequestMapping(value = "/dropCellDB", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object dropDB() throws IOException {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS cells");
			return "{\"result\":\"success\"}";
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}
	
	private Object changeOwner(Point click, String uuid, Statement stmt, Response response) throws SQLException {
		String foundItem = questDAO.setClaimed(stmt, click);
		
		response.success = StringUtils.isNotEmpty(foundItem);
		if(response.success) {
			
			JSONParser parser = new JSONParser(foundItem);
			try {
				response.foundItem = parser.anything();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			createCellTable(stmt);

			String deleteQuery = "DELETE FROM cells WHERE (x=" + click.getX() + " AND y= " + click.getY() + ")";
//				System.out.println(" --- deleteQuery: " + deleteQuery);
			stmt.executeUpdate(deleteQuery);

			int value = getCellValue(click);
//				System.out.println(" --- getCellValue: " + value);
			String insertQuery = "INSERT INTO cells VALUES (" + click.getX() + ", " + click.getY() + ", '" + uuid
					+ "', " + value + ")"; // ON CONFLICT DO UPDATE
//				System.out.println(" --- insertQuery: " + insertQuery);
			stmt.executeUpdate(insertQuery);
		}

//		Object cells = fetchCells(click.getLat(), click.getLng());
		List<Cell> cells = fetchCells(stmt);
		response.cells = cells;
		return cells;
	}

	private int getCellValue(Point click) {
		
		int hash = (click.getX() + "" + click.getY()).hashCode();
		Random generator = new Random(hash);
		
		for(int level=1; level<=20; level++) {
			int randomNumber = generateNumberBetween(generator, 1, 3);
			if(randomNumber == 1) {
				return level;
			}
		}
		
		return 20;
	}

	/** low is inclusive. high is inclusive */
	private static int generateNumberBetween(Random generator, int low, int high) {
		return generator.nextInt(high+1-low) + low;
	}
	
	private void createCellTable(Statement stmt) throws SQLException {
		String createQuery = "CREATE TABLE IF NOT EXISTS cells (x INT NOT NULL, y INT NOT NULL, uuid TEXT, value INT, CONSTRAINT cell_id_pk PRIMARY KEY (x,y))";
		//System.out.println(" --- createQuery: " + createQuery);
		stmt.executeUpdate(createQuery);
	}

	private Object fetchCells(Double lat, Double lng) {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			return fetchCells(stmt);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	private List<Cell> fetchCells(Statement stmt) throws SQLException {
		createCellTable(stmt);
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM cells");
		List<Cell> cells = new ArrayList<Cell>();
		while (rs.next()) {
			cells.add(new Cell(rs.getInt("x"), rs.getInt("y"), rs.getString("uuid"), rs.getString("value")));
		}
		return cells;
	}

	private Object saveClick(Point click, String uuid) throws IOException {

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			// stmt.executeUpdate("DROP TABLE IF EXISTS click");
			stmt.executeUpdate(
					"CREATE TABLE IF NOT EXISTS click (tick TIMESTAMP, lat DOUBLE PRECISION, lng DOUBLE PRECISION, x INT, y INT, uuid TEXT)");
			String query = "INSERT INTO click VALUES (now(), " + click.getLat() + ", " + click.getLng() + ", "
					+ click.getX() + ", " + click.getY() + ", '" + uuid + "')";
			//System.out.println(" --- query: " + query);
			stmt.executeUpdate(query);

			ResultSet rs = stmt.executeQuery("SELECT * FROM click");
			List<Object> calls = new ArrayList<Object>();
			while (rs.next()) {
				calls.add(rs.getTimestamp("tick") + ", " + rs.getInt("x") + "/" + rs.getInt("y") + ", "
						+ rs.getString("uuid"));
			}
			return calls;

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
