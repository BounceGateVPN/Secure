package com.github.smallru8.Secure.UserData;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import org.java_websocket.WebSocket;

import com.github.smallru8.util.SHA;

/**
 * 保存client資料用
 * 使用Switch時要加上public Port sport;
 * @author smallru8
 *
 */
public class SessionData extends UsrData{
	
	private WebSocket conn;
	
	/**
	 * 新增Client
	 * @param conn
	 */
	public SessionData(WebSocket conn,String name) {
		this.conn = conn;
		Name = name;
		try {
			setUserNameUUID();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WebSocket getConnection() {
		return conn;
	}
	
	private void setUserNameUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		UUID = SHA.SHA512(Name);
	}
}
