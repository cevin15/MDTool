package com.youbenzi.testcase;

import java.io.File;

import com.youbenzi.mdtool.tool.MDTool;


public class Test {

	@org.junit.Test
	public void test() {
		String result = MDTool.markdown2Html(new File(Test.class.getResource("/file.md").getFile()));
		System.out.println(result);
	}
}