package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cnell.basic.extraction.RelationInstance;
import cnell.basic.extraction.RelationPattern;

public class CNELL_DAO {

	private String urlTpl;

	public CNELL_DAO(String urlTpl) {
		this.urlTpl = urlTpl;
	}

	public static String currDate() {
		String pattern = "00";
		DecimalFormat df = new DecimalFormat(pattern);
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR) % 100);
		String month = df.format(date.get(Calendar.MONTH) + 1);
		String day = df.format(date.get(Calendar.DATE));
		String dateStr = year + month + day;
		return dateStr;
	}

	public List<String[]> getToday(String type1, String type2) {
		String dateStr = currDate();
		return getPatternRecord(dateStr, type1, type2);
	}

	public List<String[]> getPatternRecord(String date, String type1,
			String type2) {
		List<String[]> ret = new ArrayList<String[]>();
		// for test
		String url_str = String.format(urlTpl, date, type1, type2);
		String response = excuteGet(url_str);
		System.out.println(response);
		if (!response.contains("=")) {
			// empty return empty
			return ret;
		}
		String[] split = response.split("&&");
		for (String line : split) {
			if (!line.isEmpty()) {
				String[] splitLink = line.split("::");
				String[] parsedResult = new String[4];
				RelationPattern rp = RelationPattern.parse(splitLink[0]);
				RelationInstance ri = RelationInstance.parse(splitLink[0]);
				parsedResult[0] = ri.getArg1().getWord();
				parsedResult[1] = rp.getPattern();
				parsedResult[2] = ri.getArg2().getWord();
				parsedResult[3] = parseLink(splitLink[1]);
				ret.add(parsedResult);
			}
		}
		return ret;
	}

	private String parseLink(String originLink) {
		String link = "http://";
		int splitPos, cn, com, end;
		splitPos = originLink.indexOf("_");
		String tempLink = originLink.substring(splitPos + 1);
		cn = tempLink.indexOf(".cn");
		com = tempLink.indexOf(".com");
		end = tempLink.lastIndexOf(".");
		String head, tail, middle;
		System.out.println("Parse link:" + originLink);
		if (cn > com) {
			head = tempLink.substring(0, cn + 3);
			middle = tempLink.substring(cn + 3, end).replace(".", "/");
			tail = tempLink.substring(end);
		} else {
			head = tempLink.substring(0, com + 4);
			middle = tempLink.substring(com + 4, end).replace(".", "/");
			tail = tempLink.substring(end);
		}
		link += head + middle + tail;
		return link;
	}

	public static String excuteGet(String url_str) {
		System.out.println(url_str);
		// grap page
		URL url = null;
		try {
			url = new URL(url_str);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String charset = "utf-8";
		int sec_cont = 1000;
		try {
			URLConnection url_con = url.openConnection();
			url_con.setDoOutput(true);
			url_con.setReadTimeout(10 * sec_cont);
			url_con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			InputStream htm_in = url_con.getInputStream();
			String htm_str = InputStream2String(htm_in, charset);
			return htm_str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String InputStream2String(InputStream in_st, String charset)
			throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(in_st,
				charset));
		StringBuffer res = new StringBuffer();
		String line = "";
		while ((line = buff.readLine()) != null) {
			res.append(line);
		}
		return res.toString();
	}

	public static void main(String[] args) {
		CNELL_DAO dao = new CNELL_DAO(
				"http://10.141.208.27:8080/CNELL/CNELLServlet?date=%s&type=%s-%s");
		List<String[]> list = dao.getPatternRecord("150401", "ORG", "PERSON");
		for (String[] strings : list) {
			System.out.printf("%s %s %s %s\n", strings[0], strings[1],
					strings[2], strings[3]);
		}
	}
}
