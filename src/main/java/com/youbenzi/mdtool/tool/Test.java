package com.youbenzi.mdtool.tool;

import java.io.File;
import java.util.Arrays;

public class Test {

	public void test() {
		String fileName = Test.class.getResource("/").getFile();
		System.out.println(fileName);
		File file = new File(fileName.substring(0, fileName.length() - "MDTool/build/classes/main/".length()) + "tmp_1");
		System.out.println(file.exists());
		file.mkdir();
		System.out.println(file.exists());
		System.out.println(file.getPath());
		System.out.println("end");
	}
	
	public static void main(String[] args) {
		new Test().test();
	}
}
