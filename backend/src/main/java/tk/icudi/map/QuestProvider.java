package tk.icudi.map;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class QuestProvider {

	private static String geosearchUrlTemplate = "https://de.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gslimit=50&gscoord={0}|{1}&gsprop=type&format=json";
	
	private static String imageNameUrlTemplate = "https://de.wikipedia.org/w/api.php?action=query&pageids={0}&prop=pageimages&pithumbsize=400&format=json";
//	private static String imageFileUrlTemplate = "https://commons.wikimedia.org/wiki/Special:FilePath/{0}?width=400";
	
	
	@Autowired
	private QuestDAO questDAO;
	
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
		if(response.getStatusCode().is2xxSuccessful()){
			try {
				JsonNode quest = questDAO.saveFreeQuest(response, uuid);
				return addInfos(quest);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String stackTrace = sw.toString();

				return "error: " + e + "\n" + stackTrace;
			}
		} else {
			System.out.println("error: " + response);
		}

		return "error";
	}

	private String addInfos(JsonNode quest) {
		JSONObject result = new JSONObject();
		result.put("quest", new JSONObject(quest.toString()));
		
		String pageid = quest.get("pageid").asText();
		
		String imageNameURL = MessageFormat.format(imageNameUrlTemplate, pageid);	
		System.out.println(" --- imageNameURL: " + imageNameURL);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseImageName = restTemplate.getForEntity(imageNameURL, String.class);
		if(responseImageName.getStatusCode().is2xxSuccessful()){
			try {
				String body = responseImageName.getBody();
				JSONObject image = getImage(body);
				
				if(image != null) {
					String imageFileURL = image.getString("source");
					System.out.println(" --- imageFileURL: " + imageFileURL);			
					result.put("imageFileURL", imageFileURL);
				}

				System.out.println(" --- result: " + result);
				
				return result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("error: " + responseImageName);
		}

				
		return null;
	}

	private JSONObject getImage(String body) {
		JSONObject jsonObject = new JSONObject(body);
		JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");
		JSONObject page = pages.getJSONObject(pages.keys().next());
		if(page.has("thumbnail")) {
			return page.getJSONObject("thumbnail");
		}
		return null;
	}

	/** low is inclusive. high is inclusive */
	private static int generateNumberBetween(Random generator, int low, int high) {
		int diff = high+1-low;
		return generator.nextInt(diff) + low;
	}

	@RequestMapping(value = "/getNearestQuest", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object getNearestQuest(@RequestParam String lat, @RequestParam String lng, @RequestParam String uuid) throws IOException {
		
		String usedURL = MessageFormat.format(geosearchUrlTemplate, lat, lng);	
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(usedURL, String.class);
		if(response.getStatusCode().is2xxSuccessful()){
			try {
				JsonNode hit = questDAO.saveQuest(response, uuid);
				return addInfos(hit);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String stackTrace = sw.toString();

				return "error: " + e + "\n" + stackTrace;
			}
		} else {
			return "error: failed call";
		}


	}
	
	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

}
