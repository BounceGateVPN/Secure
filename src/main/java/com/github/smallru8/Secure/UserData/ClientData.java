package com.github.smallru8.Secure.UserData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;

import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.Config.NormalConfig;

/**
 * 這個Class會被包在WS client的Class中
 * WS client發出請求給server時需要的資料
 * AES256加密解密
 * RSA2048私鑰
 * @author smallru8
 *
 */
public class ClientData extends UsrData{
	
	public String sessionName;//這個連線的名稱 使用者輸入(作為config命名用)
	
	public String IPaddr;//server ip
	public int port;//server port
	private NormalConfig nc;//讀config拿到private key
	
	public ClientData(String sessionName) throws IOException {
		this.sessionName = sessionName;
		nc = new NormalConfig(this.sessionName);
		readDestCfg();
		try {
			setUserNameUUID();//設定Name跟UUID
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RSAPrivateKey getPrivateKey() {
		if(nc.isDefault)//使用Default設定
			return Secure.dc.getPrivateKey();
		return nc.getPrivateKey();
	}
	
	private void setUserNameUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//看Name是用Default.conf或<sessionName>.conf
		if(nc.isDefault) 
			Name = Secure.dc.UsrName;
		else
			Name = nc.UsrName;
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(Name.getBytes("utf-8"));
		UUID = String.format("%0128x", new BigInteger(1, digest.digest()));
	}
	
	private void readDestCfg() {
		Properties ConfigProperties = new Properties();
		try {
			ConfigProperties.load(new FileInputStream(nc.cfgDirPath + "dest.conf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPaddr = ConfigProperties.getProperty("IP","ws://127.0.0.1");
		port = Integer.parseInt(ConfigProperties.getProperty("Port","80"));
		ConfigProperties.clear();
	}
	
}
