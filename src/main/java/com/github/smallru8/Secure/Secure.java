package com.github.smallru8.Secure;

import java.io.IOException;

/**
 * 身分驗證
 * @author smallru8
 *
 */
public class Secure {

	public static ConfigReader cr;
	public static SQL sql;
	
	public Secure() {
		try {
			cr = new ConfigReader();
			sql = new SQL();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
