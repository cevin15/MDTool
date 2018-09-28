package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.bean.Block;
import com.youbenzi.mdtool.markdown.bean.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.CommonTextBuilder;
import com.youbenzi.mdtool.tool.Tools;

/**
 * markdown语法过滤器
 * 
 * @author yangyingqiang
 * 2017年7月14日 上午11:38:51
 */
public abstract class SyntaxFilter {

	private SyntaxFilter nextFilter = null;

	public abstract List<TextOrBlock> invoke(List<String> lines);

	public SyntaxFilter(SyntaxFilter nextFilter) {
		super();
		this.nextFilter = nextFilter;
	}

	public List<Block> call(String content) {
		List<TextOrBlock> textOrBlocks = invoke(Tools.read2List(content));
		List<Block> result = new ArrayList<>();
		for (TextOrBlock textOrBlock : textOrBlocks) {
			if (textOrBlock.isBlock()) {
				result.add(textOrBlock.getBlock());
				continue;
			}
			String text = textOrBlock.getText();
			if (nextFilter != null) {
				result.addAll(nextFilter.call(text));
			} else {
				result.addAll(buildCommonTextBlock(text));
			}
		}
		return result;
	}
	
	/**
	 * 将内容当成普通的文本处理
	 * @param text 内容
	 * @return 处理结果
	 */
	private List<Block> buildCommonTextBlock(String text) {
		List<Block> result = new ArrayList<>();
		List<String> lines = Tools.read2List(text);
		StringBuilder commonText = new StringBuilder();
		for (int idx = 0, l = lines.size(); idx < l; idx++) {
			String line = lines.get(idx);
			if (idx + 1 < l) {
				String nextLine = lines.get(idx + 1);
				commonText.append(line + "\n");
				if (nextLine.trim().equals("")) {
					if(!commonText.toString().equals("")) {
						result.add(new CommonTextBuilder(commonText.toString()).bulid());
						commonText = new StringBuilder();
						idx ++;
					}
				}
			} else {
				commonText.append(line + "\n");
			}
		}
		if(!commonText.toString().equals("")) {
			result.add(new CommonTextBuilder(commonText.toString()).bulid());
		}
		
		return result;
	}
}
