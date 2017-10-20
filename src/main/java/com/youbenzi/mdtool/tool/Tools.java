package com.youbenzi.mdtool.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
	
	public static List<String> read2List(String target) {
		List<String> lines = new ArrayList<String>();
		try (BufferedReader reader = new BufferedReader(new StringReader(target));) {
			String tmp = reader.readLine();
			while (tmp != null) { // 将内容每一行都存入list中
				lines.add(tmp);
				tmp = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}
	
	
    /**  
     * 过滤所有HTML标签  
     * @param str  目标内容
     * @return 过滤结果
     */  
    public static String filterHtml(String str) {   
        Pattern pattern = Pattern.compile(regxpForHtml);   
        Matcher matcher = pattern.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result = matcher.find();   
        while (result) {   
            matcher.appendReplacement(sb, "");   
            result = matcher.find();   
        }   
        matcher.appendTail(sb);   
        return sb.toString();   
    }
    
    public static void main(String[] args) {
		String t = "1. 123\n" + 
				"2. 123123\n" + 
				"	3. 12312\n" + 
				"	4. 123123\n" + 
				"	5. 12312\n" + 
				"1. 13123";
		System.out.println(t.indexOf("\t"));
		System.out.println(t.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
	}
}
