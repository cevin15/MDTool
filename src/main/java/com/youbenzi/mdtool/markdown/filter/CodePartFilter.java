package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.CodeBuilder;

/**
 * 筛选出内容中的代码块
 * @author yangyingqiang
 * 2017年7月14日 上午11:43:37
 */
public class CodePartFilter extends SyntaxFilter{

	public CodePartFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();

		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String str = lines.get(idx);
			if (str.trim().startsWith(MDToken.CODE)) { // 检测到有代码片段
				StringBuilder interText = new StringBuilder();
				boolean isCodeEnd = false;
				for (int idx1 = (idx + 1); idx1 < si; idx1++) {
					str = lines.get(idx1);
					if (str.trim().equals(MDToken.CODE)) { // 检查是否有代码结束符
						isCodeEnd = true;
						idx = idx1;
						break;
					} else {
						interText.append(str + "\n");
					}
				}

				if (isCodeEnd) { // 确定有代码片段
					if (!outerText.toString().equals("")) {
						textOrBlocks.add(new TextOrBlock(outerText.toString()));
						outerText = new StringBuilder();
					}
					textOrBlocks.add(new TextOrBlock(new CodeBuilder(interText.toString()).bulid()));
				} else { // 没代码结束符，则该代码块不完整，当成普通text来处理
					String content = outerText.append(MDToken.CODE).append("\n").append(interText.toString())
							.toString();
					textOrBlocks.add(new TextOrBlock(content));
					outerText = new StringBuilder();
					break;
				}
			} else {
				outerText.append(str + "\n");
			}
		}
		if (!outerText.toString().equals("")) {
			textOrBlocks.add(new TextOrBlock(outerText.toString()));
		}
		
		return textOrBlocks;
	}

}
