package com.youbenzi.mdtool.markdown.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.CommonTextBuilder;

/**
 * markdown语法过滤器
 * @author yangyingqiang
 * @time 2017年7月14日 上午11:38:51
 */
public abstract class SyntaxFilter {
	
	private SyntaxFilter nextFilter = null;
	
	public abstract List<TextOrBlock> invoke(List<String> lines);
	
	public SyntaxFilter(SyntaxFilter nextFilter) {
		super();
		this.nextFilter = nextFilter;
	}
	
	public List<Block> call(String content) {
		
		List<TextOrBlock> textOrBlocks = invoke(read2List(content));
		List<Block> result = new ArrayList<>();
		for (TextOrBlock textOrBlock : textOrBlocks) {
			if (textOrBlock.isBlock()) {
				result.add(textOrBlock.getBlock());
				continue;
			} 
			String text = textOrBlock.getText();
			if(nextFilter != null) {
				result.addAll(nextFilter.call(text));
			} else {
				List<String> lines = read2List(text);
				for (String str : lines) {
					result.add(new CommonTextBuilder(str).bulid());
				}
			}
		}
		return result;
	}

	private List<String> read2List(String target) {
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
