package com.github.smallru8.Secure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfigReader {
	
	public ConfigReader() throws IOException {
		
		if(!new File("config").exists()) {
			new File("config").mkdirs();
		}
		if(!new File("config/SQL").exists()) {
			new File("config/SQL").mkdirs();
		}
		if(!new File("config/SQL/SQL.properties").exists()) {
			new File("config/SQL/SQL.properties").createNewFile();
			FileWriter SQLProperties = new FileWriter("config/SQL/SQL.properties");
			SQLProperties.write("SQL = false\n");
			SQLProperties.write("host = jdbc:mysql://localhost/db\n");
			SQLProperties.write("username = user\n");
			SQLProperties.write("password = passwd\n");
			SQLProperties.close();
			
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:config/SQL/Secure.db");
				stmt = c.createStatement();
				String sqlStmt = "CREATE TABLE USER ";
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			}
			
		}
	}
	
}
