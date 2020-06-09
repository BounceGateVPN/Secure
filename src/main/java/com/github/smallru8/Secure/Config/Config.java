package com.github.smallru8.Secure.Config;

import java.io.File;
import java.io.IOException;

import com.github.smallru8.Secure.Log.Log;

public class Config {
	
	public final String ModuleName = "Config";
	public boolean UsingSQL = false;
	protected String userName = "";
	protected String passwd = "";
	protected String host = "";
	
	public String cfgName;
	public String cfgDirPath;// config/<name>/
	
	public Config(String name) {
		cfgName = name;
		cfgDirPath = "config/" + cfgName + "/";
		createCfgDir();
	}
	
	private void createCfgDir() {
		if(!new File("config").exists()) {
			new File("config").mkdirs();
			Log.printMsg(ModuleName, Log.MsgType.info, "Creating ./config");
		}
		if(!new File(cfgDirPath).exists()) {
			new File(cfgDirPath).mkdirs();
			Log.printMsg(ModuleName, Log.MsgType.info, "Creating ./" + cfgDirPath);
		}
		if(!new File(cfgDirPath + "SQL").exists()) {
			new File(cfgDirPath + "SQL").mkdirs();
			Log.printMsg(ModuleName, Log.MsgType.info, "Creating ./" + cfgDirPath + "SQL");
		}
		if(!new File(cfgDirPath + "key").exists()) {
			new File(cfgDirPath + "key").mkdirs();
			Log.printMsg(ModuleName, Log.MsgType.info, "Creating ./" + cfgDirPath + "key");
		}
	}
	
	/**
	 * 創建 <cfgName>.conf
	 * @throws IOException
	 */
	public void createCfgFile() throws IOException {
		new File(cfgDirPath + cfgName + ".conf").createNewFile();
	}
	
}
