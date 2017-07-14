package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.HeaderBuilder;

/**
 * 筛选出内容中的标题格式（同一行的，比如：
 * ### 标题
 * ）
 * @author yangyingqiang
 * @time 2017年7月14日 下午3:41:26
 */
public class HeaderOneLineFilter extends SyntaxFilter{

	public HeaderOneLineFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();
		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String str = lines.get(idx);
			if (str.trim().startsWith(MDToken.HEADLINE)) {
				if (!outerText.toString().equals("")) {
					textOrBlocks.add(new TextOrBlock(outerText.toString()));
					outerText = new StringBuilder();
				}
				textOrBlocks.add(new TextOrBlock(new HeaderBuilder(str).bulid()));
			} else {
				outerText.append(str).append("\n");
			}
		}
		if (!outerText.toString().equals("")) {
			textOrBlocks.add(new TextOrBlock(outerText.toString()));
		}
		return textOrBlocks;
	}

}
