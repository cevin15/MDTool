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
     * 基本功能：过滤所有HTML标签  
     * @param str  
     * @return String  
     */  
    public static String filterHtml(String str) {   
        Pattern pattern = Pattern.compile(regxpForHtml);   
        Matcher matcher = pattern.matcher(str);   
        StringBuffer sb = new StringBuffer();   
        boolean result1 = matcher.find();   
        while (result1) {   
            matcher.appendReplacement(sb, "");   
            result1 = matcher.find();   
        }   
        matcher.appendTail(sb);   
        return sb.toString();   
    }   
}
