package tk.icudi.map;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

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

	private static String geosearchUrlTemplate = "https://de.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gslimit=1&gscoord={0}|{1}&gsprop=type&format=json";
	
	@RequestMapping(value = "/getQuests", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object onClick(@RequestParam String lat, @RequestParam String lng, @RequestParam String uuid) throws IOException {

		Point center = new Point(Double.valueOf(lat), Double.valueOf(lng));
		
		Random generator = new Random(System.currentTimeMillis());
		int xInc = generateNumberBetween(generator, -5, +5);
		int yInc = generateNumberBetween(generator, -5, +5);
		//System.out.println(" --- change: " + xInc + "/" + yInc);
		Point questLocation = center.move(xInc, yInc);
		
		NumberFormat formatter = NumberFormat.getInstance(Locale.US);
		formatter.setMaximumFractionDigits(15);
		String usedLat = formatter.format(questLocation.getLat());
		String usedLng = formatter.format(questLocation.getLng());
		
		String usedURL = MessageFormat.format(geosearchUrlTemplate, usedLat, usedLng);	
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(usedURL, String.class);

		if(!response.getStatusCode().is2xxSuccessful()){
			System.out.println("error: " + response);
		}

		return response.getBody();
	}

	/** low is inclusive. high is inclusive */
	private static int generateNumberBetween(Random generator, int low, int high) {
		int diff = high+1-low;
		return generator.nextInt(diff) + low;
	}

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

}
