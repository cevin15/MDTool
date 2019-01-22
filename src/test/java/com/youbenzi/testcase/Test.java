package com.youbenzi.testcase;

import java.io.File;
import java.io.IOException;

import com.youbenzi.mdtool.tool.MDTool;


public class Test {

	@org.junit.Test
	public void test() throws IOException {
		String result = MDTool.markdown2Html(new File(Test.class.getResource("/fileGBK.md").getFile()), "GBK");
		System.out.println(result);
	}
}