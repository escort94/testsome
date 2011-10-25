package cn.com.jit.ida.ca.display;

import java.io.File;

public abstract class DisplayOperate {
	
	public abstract void operate();
	
	public static boolean isBeenInit_noPrint() {
		int i = 0;
		boolean bool = false;
		File localFile = new File("./config/CAConfig.xml");
		bool = localFile.exists();
		return bool;
	}
}
