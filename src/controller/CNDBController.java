package controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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

	private static final String DBPEDIA_SITE_SEARCH = "/KnowledgeWorks/cndbpedia/search?word=%s";
	private static final String ATTRIBUTE_DBPEDIA_TEMPLATE = "<a  title=\"%s\" href=\"%s\" target=\"_blank\" >%s</a>";
	private static final String ATTRIBUTE_VALUE_HTML_TEMPLATE = "<a href=\"%s\">%s</a>";
	private CNDB_DAO db = new CNDB_DAO();

	private static boolean DEBUG = true;

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
		List<Triple<String, String, String>> parameters = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> multisenses = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> information = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> infobox = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> category = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> eclass = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> eclass2 = new ArrayList<Triple<String, String, String>>();
		List<Triple<String, String, String>> entity = new ArrayList<Triple<String, String, String>>();
		if (word.isEmpty()) {
			parameters.add(new Triple<String, String, String>(word, "status",
					"emptyword"));
		} else {
//			try {
//				word = new String(word.getBytes("ISO-8859-1"), "utf-8");
//			} catch (UnsupportedEncodingException e) {
//				// debug
//			}
			System.out.println("CNDBController: Receive word=" + word);
			// mapping from word to entity
			String entityName = null;
			try { // direct -> redirect -> multisense
				String direct = db.getDirect(word);
				if (DEBUG) {
					System.out.println("direct: " + direct);
				}
				if (direct == null) {
					// check redirect
					String redirect = db.getReDirect(word);
					if (DEBUG) {
						System.out.println("redirect: " + redirect);
					}
					if (redirect == null) {
						// check multisense
						db.getMultiSenses(word, multisenses);
						if (DEBUG) {
							System.out.println("multisenses: "
									+ multisenses.size());
						}
						if (multisenses.size() == 0) {
							parameters.add(new Triple<String, String, String>(
									word, "status", "notfound"));
						} else {
							parameters.add(new Triple<String, String, String>(
									word, "status", "multisense"));
							processMultiSenses(word, multisenses);
						}
					} else {
						entityName = redirect;
						parameters.add(new Triple<String, String, String>(word,
								"status", "redirect"));
						fetchAllData(entityName, information, infobox,
								category, eclass, eclass2, entity);
					}
				} else {
					entityName = direct;
					parameters.add(new Triple<String, String, String>(word,
							"status", "direct"));
					fetchAllData(entityName, information, infobox, category,
							eclass, eclass2, entity);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// strange exceptions
				// catch and handle later
				parameters.add(new Triple<String, String, String>(word,
						"status", "exception"));
			}
		}
		parameters.add(new Triple<String, String, String>(word, "word", word));
		if(DEBUG){
			System.out.println(information.toString());
			System.out.println(infobox.toString());
			System.out.println(category.toString());
		}
		model.put("Parameters", parameters);
		model.put("MultiSense", multisenses);
		model.put("Information", information);
		model.put("InfoBox", infobox);
		model.put("Category", category);
		model.put("Class", eclass);
		model.put("Class2", eclass2);
		model.put("Entity", entity);
		return new ModelAndView("search", model);
	}

	private List<Triple<String, String, String>> processMultiSenses(
			String word, List<Triple<String, String, String>> multisenses) {
		// link the multi-sense
		String valueHTML;
		for (Triple<String, String, String> sense : multisenses) {
			valueHTML = String.format(ATTRIBUTE_VALUE_HTML_TEMPLATE,
					String.format(DBPEDIA_SITE_SEARCH, sense.getArg3()),
					sense.getArg3());
			sense.setArg3(valueHTML);
		}

		// merge if attribute is the same
		Map<String, Set<String>> attrMap = new HashMap<String, Set<String>>();
		for (Triple<String, String, String> sense : multisenses) {
			String attr = sense.getArg2();
			Set<String> set = attrMap.get(attr);
			if (set != null) {
				set.add(sense.getArg3());
			} else {
				set = new HashSet<String>();
				set.add(sense.getArg3());
				attrMap.put(attr, set);
			}
		}
		multisenses.clear();
		for (Entry<String, Set<String>> en : attrMap.entrySet()) {
			valueHTML = en.getValue().toString();
			valueHTML = valueHTML.replaceAll("\\[|\\]", "");
			valueHTML = valueHTML.replace(", ", "<br>");
			multisenses.add(new Triple<String, String, String>(word, en.getKey(),
					valueHTML));
		}
		return multisenses;
	}

	private void fetchAllData(String entityName,
			List<Triple<String, String, String>> information,
			List<Triple<String, String, String>> infobox,
			List<Triple<String, String, String>> category,
			List<Triple<String, String, String>> eclass,
			List<Triple<String, String, String>> eclass2,
			List<Triple<String, String, String>> entity) {
		entityName = entityName.replace("\r", "").replace("\n", "");
		db.getInformation(entityName, information);
		db.getInfobox(entityName, infobox);
		processInfobox(entityName, infobox);
		db.getCategory(entityName, category);
		db.getClass(entityName, eclass);
		db.getClass2(entityName, eclass2);
		db.getSameAs(entityName, entity);
		// remove duplicated
		rmdup(information);
		rmdup(infobox);
	}

	private List<Triple<String, String, String>> processInfobox(
			String entityName, List<Triple<String, String, String>> infobox) {
		List<Triple<String, String, String>> attributeMapping = new ArrayList<Triple<String, String, String>>();
		db.getAttributeMapping(attributeMapping);
		// check if value is entity
		for (Triple<String, String, String> info : infobox) {
			String value = info.getArg3();
			value = value.trim().replace("\r", "").replace("\n", "");
			// detect HTML <a> <br> segment
			String entityLinkHTML = processHTMLTag(value);
			info.setArg3(entityLinkHTML);
		}

		// merge if attribute is the same
		Map<String, List<String>> attrMap = new HashMap<String, List<String>>();
		for (Triple<String, String, String> info : infobox) {
			String attr = info.getArg2();
			List<String> set = attrMap.get(attr);
			if (set != null) {
				set.add(info.getArg3());
			} else {
				set = new LinkedList<String>();
				set.add(info.getArg3());
				attrMap.put(attr, set);
			}
		}

		// attribute mapping
		infobox.clear();
		for (Entry<String, List<String>> en : attrMap.entrySet()) {
			boolean mappingHas = false;
			for (Triple<String, String, String> attr : attributeMapping) {
				if (en.getKey().equals(attr.getArg1())) {
					mappingHas = true;
					// link attribute template
					String attrHTML = String.format(ATTRIBUTE_DBPEDIA_TEMPLATE,
							attr.getArg2(), attr.getArg3(), attr.getArg1());
					String valueHTML = toHTMLList(en.getValue());
					infobox.add(new Triple<String, String, String>(
							entityName, attrHTML, valueHTML));
				}
			}
			if (!mappingHas) {
				String valueHTML = toHTMLList(en.getValue());
				infobox.add(new Triple<String, String, String>(
						entityName, en.getKey(), valueHTML));
			}
		}
		return infobox;

	}

	private String toHTMLList(List<String> list) {
		String html = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			html+="<br>"+list.get(i);
		}
		return html;
	}

	private String processHTMLTag(String value) {
		if(!value.contains("<a>")){
			return value;
		}
		
		Pattern p = Pattern.compile("(<a>(.*)</a>)+");
		Matcher m = p.matcher(value);
		String result = value;
		String linkUrl;
		String group;
		String entity;
		while (m.find()) {
			group = m.group();
			entity = m.group(2);
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
		a.search("复旦大学");
//		a.search("樱桃电视剧");
//		a.search("徐江洪");
	}
}
