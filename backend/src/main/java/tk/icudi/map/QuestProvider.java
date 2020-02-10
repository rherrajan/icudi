package tk.icudi.map;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class QuestProvider {

	private static String geosearchUrlTemplate = "https://de.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gslimit=50&gscoord={0}|{1}&gsprop=type&format=json";

	@RequestMapping(value = "/getQuests", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object onClick(@RequestParam String lat, @RequestParam String lng, @RequestParam String uuid) throws IOException {

		System.out.println(" --- geosearchUrlTemplate: " + geosearchUrlTemplate);
		String usedURL = MessageFormat.format(geosearchUrlTemplate, lat, lng);
		System.out.println(" --- geosearchUrlTemplate: " + usedURL);
		
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.getForEntity(usedURL, String.class);

		System.out.println(" --- response: " + response);
		System.out.println("  --- getStatusCode: " + response.getStatusCode());

		return response.getBody();
	}

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

}
