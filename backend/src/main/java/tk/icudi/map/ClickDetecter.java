package tk.icudi.map;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
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
		
	@RequestMapping(value="/click", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object onClick(@RequestParam Double lat, @RequestParam Double lng, @RequestParam String uuid) throws IOException {
		Point click = new Point(lat, lng);
		return save(click, uuid); 
	}

	private Object save(Point click, String uuid) throws IOException {

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			//stmt.executeUpdate("DROP TABLE IF EXISTS click");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS click (tick TIMESTAMP, lat DOUBLE PRECISION, lng DOUBLE PRECISION, x INT, y INT, uuid TEXT)");
			String query = "INSERT INTO click VALUES (now(), "+ click.getLat()+", "+ click.getLng()+", " + click.getX() +", "+click.getY()+", '" + uuid + "')";
			System.out.println(" --- query: " + query);
			stmt.executeUpdate(query);
			ResultSet rs = stmt.executeQuery("SELECT * FROM click");

			List<Object> calls = new ArrayList<Object>();
			while (rs.next()) {
				calls.add(rs.getTimestamp("tick") + ", " + rs.getInt("x") + "/" + rs.getInt("y") + ", " + rs.getString("uuid"));
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
