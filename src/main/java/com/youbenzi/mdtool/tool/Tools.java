package com.youbenzi.mdtool.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Tools {

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
	
}
