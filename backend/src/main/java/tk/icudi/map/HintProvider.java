package tk.icudi.map;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HintProvider {
	
	@Autowired
	private QuestDAO questDAO;
	
	@RequestMapping(value="/getHint", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Object getHint(@RequestParam Double lat, @RequestParam Double lng, @RequestParam String uuid) throws IOException {
		return questDAO.getNearestHit(lat, lng, uuid);
	}
	
	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
	    response.setHeader("Access-Control-Allow-Origin", "*");	    
	}   
	
}
