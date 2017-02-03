package com.youbenzi.mdtool.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.youbenzi.mdtool.export.HTMLDecorator;
import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.MDAnalyzer;

public class MDTool {

	public static String markdown2Html(File file){
		BufferedReader reader = null;
		String lineStr = null;
		try {
			reader = new BufferedReader(new FileReader(file));StringBuffer sb = new StringBuffer();
			while ((lineStr = reader.readLine())!=null) {
				sb.append(lineStr).append("\n");
			}
			return markdown2Html(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String markdown2Html(String mdStr){
		if(mdStr==null){
			return null;
		}
		BufferedReader reader = new BufferedReader(new StringReader(mdStr));
		List<Block> list = MDAnalyzer.analyze(reader);
		
		HTMLDecorator decorator = new HTMLDecorator(); 
		
		decorator.decorate(list);
		
		return decorator.outputHtml();
	}
	
	public static void main(String[] args) {
		System.out.println(MDTool.markdown2Html(new File("test_file/file1.md")));
	}
}
