package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DAO {

	protected Connection conn = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;

	private String database;
	private String user = "root";
	private String password = "root";

	public DAO(String database) {
		this.database = database;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载mysq驱动
		} catch (ClassNotFoundException e) {
			System.out.println("Find no driver for sql");
			e.printStackTrace();// 打印出错详细信息
		}
	}

	public DAO(String database, String user, String password) {
		this.database = database;
		this.user = user;
		this.password = password;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载mysq驱动
		} catch (ClassNotFoundException e) {
			System.out.println("Find no driver for sql");
			e.printStackTrace();// 打印出错详细信息
		}
	}

	protected void connect() {
		try {
			conn = DriverManager.getConnection(database, user, password);
		} catch (SQLException e) {
			System.out.println("数据库链接错误");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
			System.out.println("Close error");
			e.printStackTrace();
		}
	}
}
