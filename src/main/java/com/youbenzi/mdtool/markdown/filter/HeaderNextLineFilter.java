package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.HeaderBuilder;

/**
 * 筛选出内容中的标题格式（两行的，比如：
 * 标题
 * ====
 * ）
 * @author yangyingqiang
 * 2017年7月14日 下午4:15:52
 */
public class HeaderNextLineFilter extends SyntaxFilter {

	public HeaderNextLineFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();
		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String str = lines.get(idx);
			if ((idx + 1) < si) {
				String nextStr = lines.get(idx + 1);
				int lvl = HeaderBuilder.isRightType(nextStr);
				if (lvl > 0) {
					if (!outerText.toString().equals("")) {
						textOrBlocks.add(new TextOrBlock(outerText.toString()));
						outerText = new StringBuilder();
					}
					textOrBlocks.add(new TextOrBlock(new HeaderBuilder(str).bulid(lvl)));
					idx++;
				} else {
					outerText.append(str).append("\n");
				}
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
