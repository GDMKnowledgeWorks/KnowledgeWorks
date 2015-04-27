package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dao.CNELL_DAO;

@Controller
@RequestMapping("/cnell")
public class CNELLController {
	private CNELL_DAO dao = new CNELL_DAO(
			"http://localhost:8082/CNELL/CNELLServlet?date=%s&type=%s-%s");

	@RequestMapping(value = "/")
	public ModelAndView defaultpage() {
		System.out
				.println("CNELLController: Receive empty and dispatch to introduction");
		Map<String, List<String[]>> model = new HashMap<String, List<String[]>>();
		// show today
		String date = CNELL_DAO.currDate();
		List<String[]> today = dao.getPatternRecord(date, "ORG", "PERSON");
		if (today.size() == 0) {
			List<String[]> status = emptyStatus(date);
			model.put("status", status);
		}
		model.put("data", today);
		return new ModelAndView("intro", model);
	}

	@RequestMapping(value = "/introduction")
	public ModelAndView intro() {
		return defaultpage();
	}

	@RequestMapping("/relation")
	public ModelAndView specifiedDate(
			@RequestParam(value = "date", required = false, defaultValue = "") String date,
			HttpServletRequest request) {
		System.out.printf("CNELLController: Receive relation date %s\n", date);
		if (date.isEmpty()) {
			date = CNELL_DAO.currDate();
		}
		Map<String, List<String[]>> model = new HashMap<String, List<String[]>>();
		// show today
		List<String[]> list = dao.getPatternRecord(date, "ORG", "PERSON");
		if (list.size() == 0) {
			List<String[]> status = emptyStatus(date);
			model.put("status", status);
		}
		model.put("data", list);
		return new ModelAndView("relation", model);
	}

	private List<String[]> emptyStatus(String date) {
		List<String[]> status = new ArrayList<String[]>();
		String currDate = CNELL_DAO.currDate();
		if (date.compareTo(currDate) > 0) {
			status.add(new String[] { "1", "Date is not correct", currDate });
		}else{
			status.add(new String[] { "2", "The system is still extracting", currDate });
		}
		return status;
	}
}
