package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.HeaderBuilder;

/**
 * 筛选出内容中的标题格式（两行的，比如：
 * 标题
 * ====
 * ）
 * @author yangyingqiang
 * @time 2017年7月14日 下午4:15:52
 */
public class HeaderNextLineFilter extends SyntaxFilter{

	public HeaderNextLineFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String str = lines.get(idx);
			Block block = null;
			if ((idx + 1) < si) {
				String nextStr = lines.get(idx + 1);
				int lvl = HeaderBuilder.isRightType(nextStr);
				if (lvl > 0) {
					block = new HeaderBuilder(str).bulid(lvl);
					idx++;
				}
			} 
			if(block == null) {
				textOrBlocks.add(new TextOrBlock(str));
			} else {
				textOrBlocks.add(new TextOrBlock(block));
			}
		}
		return textOrBlocks;
	}

}
