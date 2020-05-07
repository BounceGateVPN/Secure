package com.github.smallru8.Secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConfigReader {
	
	private final String SQLPropertiesPath = "config/SQL/SQL.properties";
	public boolean UsingSQL = false;
	private String userName;
	private String passwd;
	private String host;
	
	public ConfigReader() throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.err.println("SQL driver not found.");
			e1.printStackTrace();
		}
		if(!new File("config").exists()) {
			new File("config").mkdirs();
		}
		if(!new File("config/SQL").exists()) {
			new File("config/SQL").mkdirs();
		}
		Connection c = null;
		Statement stmt = null;
		if(!new File(SQLPropertiesPath).exists()) {
			new File(SQLPropertiesPath).createNewFile();
			FileWriter SQLProperties = new FileWriter(SQLPropertiesPath);
			SQLProperties.write("SQL = false\n");
			SQLProperties.write("host = jdbc:mysql://localhost/db\n");
			SQLProperties.write("username = user\n");
			SQLProperties.write("password = passwd\n");
			SQLProperties.close();

			try {
				//建DB
				c = DriverManager.getConnection("jdbc:sqlite:config/SQL/Secure.db");
				stmt = c.createStatement();
				//建Table
				String sqlStmt1 = "CREATE TABLE USER"+"(UUID VARCHAR(128),PASSWD VARCHAR(500),Session VARCHAR(128),LastLogInTime INT)charset=utf8;";
				stmt.executeUpdate(sqlStmt1);
				c.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.err.println( e.getClass().getName() + ": " + e.getMessage());
			}
		}else {
			Properties SQLproperties = new Properties();
			SQLproperties.load(new FileInputStream(SQLPropertiesPath));
			
			//有啟用mysql模式
			if(SQLproperties.getProperty("SQL","false").startsWith("true")){
				UsingSQL = true;
				host = SQLproperties.getProperty("host","jdbc:mysql://localhost/db");
				userName = SQLproperties.getProperty("username","user");
				passwd = SQLproperties.getProperty("password","passwd");
				checkSQLTable();
			}
		}
		if(!new File("key").exists()) {
			new File("key").mkdir();
		}
	}
	
	/**
	 * Table檢查
	 * @return
	 */
	private boolean checkSQLTable() {
		Connection conn;
		try {
			conn = DriverManager.getConnection(host,userName,passwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Can not connect to SQL server.");
			e.printStackTrace();
			return false;
		}
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "USER", null);
			if (!tables.next()) {
				String sqlStmt1 = "CREATE TABLE USER"+"(UUID VARCHAR(128),PASSWD VARCHAR(500),Session VARCHAR(128),LastLogInTime INT)charset=utf8;";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sqlStmt1);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
}
