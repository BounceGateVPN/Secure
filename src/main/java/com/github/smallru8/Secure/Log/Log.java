package com.github.smallru8.Secure.Log;

public class Log {

	public enum MsgType{
		info,err,warn
	}
	
	public static void printMsg(String moduleName,MsgType type,String msg) {
		if(type.equals(MsgType.info)) {
			System.out.print("[Info]");
		}else if(type.equals(MsgType.err)) {
			System.out.print("[Error]");
		}else if(type.equals(MsgType.warn)) {
			System.out.print("[Warn]");
		}
		System.out.println("["+moduleName+"] "+msg);
	}
	
}
