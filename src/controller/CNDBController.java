package controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import structure.Triple;
import dao.CNDB_DAO;

@Controller
@RequestMapping("/cndbpedia")
public class CNDBController {

	private static final String DBPEDIA_SITE_SEARCH = "http://gdm.fudan.edu.cn/KnowledgeWorks/cndbpedia/search?word=%s";
	private static final String ATTRIBUTE_DBPEDIA_TEMPLATE = "<a  title=\"%s\" href=\"%s\" target=\"_blank\" >%s</a>";
	private static final String ATTRIBUTE_VALUE_HTML_TEMPLATE = "<a href=\"%s\">%s</a>";
	private CNDB_DAO db = new CNDB_DAO();

	@RequestMapping(value = "/")
	public String defaultpage(Model model) {
		System.out
				.println("CNDBController: Receive empty and dispatch to introduction");
		return "intro";
	}

	@RequestMapping(value = "/introduction")
	public String introduction(Model model) {
		System.out.println("CNDBController: Receive introduction");
		return "intro";
	}

	@RequestMapping(value = "/search")
	public ModelAndView search(
			@RequestParam(value = "word", required = false, defaultValue = "") String word) {
		Map<String, List<Triple<String, String, String>>> model = new HashMap<String, List<Triple<String, String, String>>>();
		List<Triple<String, String, String>> information = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> infobox = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> category = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> eclass = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> eclass2 = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> entity = new ArrayList<Triple<String, String, String>>();
		if (word.isEmpty()) {
			// return empty
		} else {
//			 try {
//			 word = new String(word.getBytes("ISO-8859-1"), "utf-8");
//			 } catch (UnsupportedEncodingException e) {
//			 // debug
//			 }
			System.out.println("CNDBController: Receive word=" + word);
			// mapping from word to entity
			String entityName;
			try {
				if (db.hasAttributeValueAsEntity(word)) {
					entityName = word;
					information = db.getInformation(entityName);
					infobox = processInfobox(db.getInfobox(entityName),
							db.getAttributeMapping(), entityName);
					category = db.getCategory(entityName);
					eclass = db.getClass(entityName);
					eclass2 = db.getClass2(entityName);
					entity = db.getSameAs(word);
					// remove duplicated
					rmdup(information);
					rmdup(infobox);
				} else if ((entityName = db.hasAlias(word)) != null) {
					// double check
					if (db.hasAttributeValueAsEntity(entityName)) {
						information = db.getInformation(entityName);
						infobox = processInfobox(db.getInfobox(entityName),
								db.getAttributeMapping(), entityName);
						category = db.getCategory(entityName);
						eclass = db.getClass(entityName);
						eclass2 = db.getClass2(entityName);
						entity = db.getSameAs(word);
						// remove duplicated
						rmdup(information);
						rmdup(infobox);
					}
				} else {
					// return empty
				}
			} catch (Exception e) {

			}
		}
		model.put("Information", information);
		model.put("InfoBox", infobox);
		model.put("Category", category);
		model.put("Class", eclass);
		model.put("Class2", eclass2);
		model.put("Entity", entity);
		return new ModelAndView("search", model);
	}

	private List<Triple<String, String, String>> processInfobox(
			List<Triple<String, String, String>> infobox,
			List<Triple<String, String, String>> attributeMapping,
			String entityName) {
		List<Triple<String, String, String>> processedInfobox = new ArrayList<Triple<String, String, String>>();
		// check if value is entity
		for (Triple<String, String, String> info : infobox) {
			String value = info.getArg3();
			value = value.trim().replace("\r", "").replace("\n", "");
			// detect HTML <a> <br> segment
			String entityLinkHTML = processHTMLTag(value);
			info.setArg3(entityLinkHTML);
		}

		// merge if attribute is the same
		Map<String, Set<String>> attrMap = new HashMap<String, Set<String>>();
		for (Triple<String, String, String> info : infobox) {
			String attr = info.getArg2();
			Set<String> set = attrMap.get(attr);
			if (set != null) {
				set.add(info.getArg3());
			} else {
				set = new HashSet<String>();
				set.add(info.getArg3());
				attrMap.put(attr, set);
			}
		}

		// attribute mapping
		for (Entry<String, Set<String>> en : attrMap.entrySet()) {
			boolean mappingHas = false;
			for (Triple<String, String, String> attr : attributeMapping) {
				if (en.getKey().equals(attr.getArg1())) {
					mappingHas = true;
					// link attribute template
					String attrHTML = String.format(ATTRIBUTE_DBPEDIA_TEMPLATE,
							attr.getArg2(), attr.getArg3(), attr.getArg1());
					String valueHTML = en.getValue().toString();
					valueHTML = valueHTML.replaceAll("\\[|\\]", "");
					processedInfobox.add(new Triple<String, String, String>(
							entityName, attrHTML, valueHTML));
				}
			}
			if (!mappingHas) {
				String valueHTML = en.getValue().toString();
				valueHTML = valueHTML.replaceAll("\\[|\\]", "");
				processedInfobox.add(new Triple<String, String, String>(
						entityName, en.getKey(), valueHTML));
			}
		}
		return processedInfobox;

	}

	private String processHTMLTag(String value) {
		Pattern p = Pattern.compile("(<a([^(<>)]*)>([^(<a>)]+)</a>)+");
		Matcher m = p.matcher(value);
		String result = value;
		String linkUrl;
		String group;
		String entity;
		while (m.find()) {
			group = m.group();
			entity = m.group(3);
			linkUrl = String.format(DBPEDIA_SITE_SEARCH, entity);
			result = result.replace(group, String.format(
					ATTRIBUTE_VALUE_HTML_TEMPLATE, linkUrl, entity));
		}
		result = result.replace("展开", "");
		return result;
	}

	private void rmdup(List<Triple<String, String, String>> information) {
		Triple<String, String, String> term1;
		Triple<String, String, String> term2;
		List<Triple<String, String, String>> rmList = new ArrayList<Triple<String, String, String>>();
		for (int i = 0; i < information.size(); i++) {
			term1 = information.get(i);
			for (int j = i + 1; j < information.size(); j++) {
				term2 = information.get(j);
				if (term1.getArg2().equals(term2.getArg2())) {
					// choose the longer one
					if (term1.getArg3().length() > term2.getArg3().length()) {
						rmList.add(term2);
					} else {
						rmList.add(term1);
					}
				}
			}
		}
		// remove
		information.removeAll(rmList);
	}

	public static void main(String[] args) {
		// test
		CNDBController a = new CNDBController();
		List<Triple<String, String, String>> infobox = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> attributeMapping = new ArrayList<Triple<String, String, String>>();
		attributeMapping.add(new Triple<String, String, String>("职业", "zhiye",
				"zhiyeurl"));
		attributeMapping.add(new Triple<String, String, String>("妻子", "qizi",
				"qiziurl"));

		infobox.add(new Triple<String, String, String>("太湖", "气候条件",
				"<a target=\"_blank\" href=\"/view/47993.htm\">亚热带季风气候</a>"));
		infobox.add(new Triple<String, String, String>(
				"太湖",
				"著名景点",
				"<a target=\"_blank\" href=\"/view/969797.htm\">大七孔桥</a>、梦塘、恐怖峡、<a target=\"_blank\" href=\"/subview/64627/11022289.htm\">天生桥</a>"));
		List<Triple<String, String, String>> processedInfobox = a
				.processInfobox(infobox, attributeMapping, "太湖");

		for (Triple<String, String, String> triple : processedInfobox) {
			System.out.println(triple);
		}

	}
}
