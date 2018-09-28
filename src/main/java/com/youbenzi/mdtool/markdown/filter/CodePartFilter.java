package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.bean.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.CodeBuilder;

/**
 * 筛选出内容中的代码块
 * @author yangyingqiang
 * 2017年7月14日 上午11:43:37
 */
public class CodePartFilter extends SyntaxFilter{

	private static final int CODE_PART_TYPE_NONE = 0;
	private static final int CODE_PART_TYPE_BLANK = 1;
	private static final int CODE_PART_TYPE_TOKEN = 2;
	
	public CodePartFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();

		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String previousLine = getFromList(lines, idx - 1);
			String currentLine = getFromList(lines, idx);
			int codePartType = isCodePartBegin(currentLine, previousLine); 
			if (codePartType != CODE_PART_TYPE_NONE) { // 检测到有代码片段
				StringBuilder interText = initCodePartText(currentLine, codePartType);
				boolean isCodeEnd = false;
				for (int idx1 = (idx + 1); idx1 < si; idx1++) {
					currentLine = getFromList(lines, idx1);
					String nextLine = getFromList(lines, idx1 + 1);
					if (isCodePartEnd(currentLine, nextLine, codePartType, interText)) { // 检查是否有代码结束符
						isCodeEnd = true;
						idx = idx1;
						break;
					} else {
						interText.append(currentLine + "\n");
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
				outerText.append(currentLine + "\n");
			}
		}
		if (!outerText.toString().equals("")) {
			textOrBlocks.add(new TextOrBlock(outerText.toString()));
		}
		
		return textOrBlocks;
	}

	private StringBuilder initCodePartText(String currentLine, int codePartType) {
		StringBuilder text = new StringBuilder();
		if (codePartType == CODE_PART_TYPE_BLANK) {
			text.append(currentLine).append("\n");
		}
		return text;
	}
	
	private int isCodePartBegin(String currentLine, String previousLine) {
		if (currentLine.trim().startsWith(MDToken.CODE)) {
			return CODE_PART_TYPE_TOKEN;
		}
		if ((previousLine == null || previousLine.trim().equals(""))) {
			return isCode(currentLine, CODE_PART_TYPE_BLANK)?CODE_PART_TYPE_BLANK:CODE_PART_TYPE_NONE;
		}
		return CODE_PART_TYPE_NONE;
	}
	
	private boolean isCodePartEnd(String currentLine, String nextLine, int codePartType, StringBuilder currentText) {
		switch (codePartType) {
		case CODE_PART_TYPE_TOKEN:
			if (currentLine.trim().startsWith(MDToken.CODE)) {
				return true;
			}
			break;
		case CODE_PART_TYPE_BLANK:
			if (nextLine == null) {
				if (!currentLine.trim().equals("")) {
					currentText.append(currentLine).append("\n");	
				}
				return true;
			}
			if (!nextLine.trim().equals("") && currentLine.trim().equals("")) {
				return true;
			}
		default:
			break;
		}
		return false;
	}
	
	private boolean isCode(String currentLine, int codePartType) {
		switch (codePartType) {
		case CODE_PART_TYPE_TOKEN:
			return true;
		case CODE_PART_TYPE_BLANK:
		default:
			return currentLine.startsWith(MDToken.CODE_BLANK);
		}
	}

	private String getFromList(List<String> list, int index) {
		try {
			return list.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
