package controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Webhooks")
public class WebhooksController {

	private static String SECRET = "fudan@188";
	private static String[] authorityArray = new String[] {"SidneyFanFan"};

	@RequestMapping(value = "/githubPush.do", method = RequestMethod.POST)
	@ResponseBody
	public String webhooks(@RequestBody String requestBody) {
		// this is a post from github push
		System.out.println(requestBody);
		JSONObject request = new JSONObject(requestBody);
		String pusher = request.getJSONObject("pusher").getString("name");
		for (String string : authorityArray) {
			if(pusher.equals(string)){
				return "OK: " + pusher;
			}
		}
		return "Failed: " + pusher+" is not authorized";
	}

}
