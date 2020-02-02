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

	@RequestMapping(value = "/click", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object onClick(@RequestParam Double lat, @RequestParam Double lng, @RequestParam String uuid) throws IOException {
		Point click = new Point(lat, lng);
		saveClick(click, uuid);

		return changeOwner(click, uuid);
	}

	@RequestMapping(value = "/getCells", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object getCells() throws IOException {
		return fetchCells();
	}

	private Object changeOwner(Point click, String uuid) {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			createCellTable(stmt);

			String deleteQuery = "DELETE FROM cells WHERE (x=" + click.getX() + " AND y= " + click.getY() + ")";
			System.out.println(" --- deleteQuery: " + deleteQuery);
			stmt.executeUpdate(deleteQuery);

			String insertQuery = "INSERT INTO cells VALUES (" + click.getX() + ", " + click.getY() + ", '" + uuid
					+ "')"; // ON CONFLICT DO UPDATE
			System.out.println(" --- insertQuery: " + insertQuery);
			stmt.executeUpdate(insertQuery);

			return fetchCells();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	private void createCellTable(Statement stmt) throws SQLException {
		// stmt.executeUpdate("DROP TABLE IF EXISTS cells");
		String createQuery = "CREATE TABLE IF NOT EXISTS cells (x INT NOT NULL, y INT NOT NULL, uuid TEXT, CONSTRAINT cell_id_pk PRIMARY KEY (x,y))";
		System.out.println(" --- createQuery: " + createQuery);
		stmt.executeUpdate(createQuery);
	}

	private Object fetchCells() {
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			createCellTable(stmt);
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM cells");
			List<Cell> cells = new ArrayList<Cell>();
			while (rs.next()) {
				cells.add(new Cell(rs.getInt("x"), rs.getInt("y"), rs.getString("uuid")));
			}
			return cells;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();

			return "error: " + e + "\n" + stackTrace;
		}
	}

	private Object saveClick(Point click, String uuid) throws IOException {

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			// stmt.executeUpdate("DROP TABLE IF EXISTS click");
			stmt.executeUpdate(
					"CREATE TABLE IF NOT EXISTS click (tick TIMESTAMP, lat DOUBLE PRECISION, lng DOUBLE PRECISION, x INT, y INT, uuid TEXT)");
			String query = "INSERT INTO click VALUES (now(), " + click.getLat() + ", " + click.getLng() + ", "
					+ click.getX() + ", " + click.getY() + ", '" + uuid + "')";
			System.out.println(" --- query: " + query);
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
