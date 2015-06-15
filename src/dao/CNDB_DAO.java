package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import structure.Triple;

public class CNDB_DAO extends DAO {

	private static String SELECT_DIRECT = "select * from directmapping where entity_sense='%s'";
	private static String SELECT_REDIRECT = "select * from redirectmapping where entity_sense='%s'";
	private static String SELECT_MULTISENSE = "select * from multisensemapping where string='%s'";

	private static String SELECT_INFORMATION = "select * from information where entity_name='%s'";
	private static String SELECT_ALIAS = "select * from aliasmapping where entity_alias='%s'";
	private static String SELECT_INFOBOX = "select * from infobox where entity_name='%s'";
	private static String SELECT_ATTRIBUTEMAPPING = "select * from attributemapping";
	private static String SELECT_CATEGORY = "select * from category where entity_name='%s'";
	private static String SELECT_CLASS = "select * from class where entity_name='%s'";
	private static String SELECT_CLASS2 = "select * from myclass2 where entity_name='%s'";
	private static String SELECT_SAMEAS = "select * from sameas where entity_name='%s'";

	public CNDB_DAO() {
		super(
				// "jdbc:mysql://10.131.239.140:3306/cndbpedia?useUnicode=true&characterEncoding=UTF-8","root","root");
				"jdbc:mysql://10.141.208.21:3306/cndbpedia?useUnicode=true&characterEncoding=UTF-8",
				"admin", "gdmlabsqladmin");
	}

	public void getInformation(String entity_name,
			List<Triple<String, String, String>> infoList) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_INFORMATION, entity_name);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				infoList.add(new Triple<String, String, String>(rs
						.getString("entity_name"), rs
						.getString("entity_information"), rs
						.getString("entity_information_value")));
			}
			super.close();
		} catch (SQLException e) {
			infoList.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getInformation");
			e.printStackTrace();
			super.close();
		}
	}

	public String getDirect(String word) {
		String entity_name = null;
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_DIRECT, word);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				entity_name = rs.getString("entity_name");
			}
			super.close();
		} catch (SQLException e) {
			entity_name = null;
			System.out.println("Data base manipulation error: getDirect");
			e.printStackTrace();
			super.close();
		}
		return entity_name;
	}

	public String getReDirect(String word) {
		String entity_name = null;
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_REDIRECT, word);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				entity_name = rs.getString("entity_name");
			}
			super.close();
		} catch (SQLException e) {
			entity_name = null;
			System.out.println("Data base manipulation error: getReDirect");
			// e.printStackTrace();
			super.close();
		}
		return entity_name;
	}

	public void getMultiSenses(String word,
			List<Triple<String, String, String>> ms) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_MULTISENSE, word);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ms.add(new Triple<String, String, String>(word, "多义词", rs
						.getString("entity_name")));
			}
			super.close();
		} catch (SQLException e) {
			System.out.println("Data base manipulation error: getMultiSenses");
			// e.printStackTrace();
			super.close();
		}
	}

	public void getAttributeMapping(
			List<Triple<String, String, String>> attrList) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_ATTRIBUTEMAPPING);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				attrList.add(new Triple<String, String, String>(rs
						.getString("entity_attribute"), rs
						.getString("type_dbpedia"), rs.getString("type_link")));
			}
			super.close();
		} catch (SQLException e) {
			System.out
					.println("Data base manipulation error: getAttributeMapping");
			// e.printStackTrace();
			super.close();
		}
	}

	public void getInfobox(String entity_name,
			List<Triple<String, String, String>> infolist) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_INFOBOX, entity_name);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				infolist.add(new Triple<String, String, String>(rs
						.getString("entity_name"), rs
						.getString("entity_attribute"), rs
						.getString("entity_attribute_value")));
			}
			super.close();
		} catch (SQLException e) {
			infolist.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getInfobox");
			// e.printStackTrace();
			super.close();
		}
	}

	public void getCategory(String entity_name,
			List<Triple<String, String, String>> list) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_CATEGORY, entity_name);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(new Triple<String, String, String>(rs
						.getString("entity_name"),
						rs.getString("entity_category"), rs
								.getString("entity_category_value")));
			}
			super.close();
		} catch (SQLException e) {
			list.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getCategory");
			// e.printStackTrace();
			super.close();
		}
	}

	public void getClass(String entity_name,
			List<Triple<String, String, String>> list) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_CLASS, entity_name);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(new Triple<String, String, String>(rs
						.getString("entity_name"),
						rs.getString("type_dbpedia"), rs.getString("type_link")));
			}
			super.close();
		} catch (SQLException e) {
			list.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getClass");
			 e.printStackTrace();
			super.close();
		}
	}

	public void getClass2(String entity_name,
			List<Triple<String, String, String>> list) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_CLASS2, entity_name);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(new Triple<String, String, String>(rs
						.getString("entity_name"),
						rs.getString("type_dbpedia"), rs.getString("type_link")));
			}
			super.close();
		} catch (SQLException e) {
			list.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getClass2");
			// e.printStackTrace();
			super.close();
		}
	}

	public void getSameAs(String entity_name,
			List<Triple<String, String, String>> list) {
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_SAMEAS, entity_name);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(new Triple<String, String, String>(rs
						.getString("entity_name"), rs
						.getString("entity_dbpedia"), rs
						.getString("entity_link")));
			}
			super.close();
		} catch (SQLException e) {
			list.add(new Triple<String, String, String>(entity_name, "信息",
					"查询失败"));
			System.out.println("Data base manipulation error: getSameAs");
			// e.printStackTrace();
			super.close();
		}
	}

	public String hasAlias(String alias) {
		String entityName = null;
		try {
			super.connect();
			stmt = conn.createStatement();
			String sql = String.format(SELECT_ALIAS, alias);

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				entityName = rs.getString("entity_name");
			}
			super.close();
		} catch (SQLException e) {
			System.out.println("Data base manipulation error: hasAlias");
			// e.printStackTrace();
			super.close();
		}
		entityName = entityName.trim().replace("\r", "").replace("\n", "");
		return entityName;
	}

	public boolean hasEntity(String word) {
		String entity_name = getDirect(word);
		return entity_name != null;
	}

	private String excute(String sql) {
		String r = "";
		try {
			super.connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				r += rs.getString("TABLE_NAME") + "\n";
			}
			super.close();
		} catch (SQLException e) {
			System.out.println("Data base manipulation error: sql");
			e.printStackTrace();
			super.close();
		}
		return r;
	}

	public static void main(String[] args) {
		CNDB_DAO d = new CNDB_DAO();
		String r = d
				.excute("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA ='cndbpedia'");
		System.out.println("result");
		System.out.println(r);

	}

}
