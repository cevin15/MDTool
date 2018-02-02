package com.youbenzi.mdtool.markdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.youbenzi.mdtool.markdown.TextLinePiece.PieceType;
import com.youbenzi.mdtool.markdown.filter.CodePartFilter;
import com.youbenzi.mdtool.markdown.filter.HeaderNextLineFilter;
import com.youbenzi.mdtool.markdown.filter.HeaderOneLineFilter;
import com.youbenzi.mdtool.markdown.filter.ListFilter;
import com.youbenzi.mdtool.markdown.filter.SyntaxFilter;
import com.youbenzi.mdtool.markdown.filter.TablePartFilter;

public class Analyzer {

	private static List<String> mdTokenInLine = Arrays.asList(MDToken.BOLD_WORD, MDToken.ITALIC_WORD,
			MDToken.ITALIC_WORD_2, MDToken.STRIKE_WORD, MDToken.CODE_WORD, MDToken.IMG, MDToken.LINK);

	/**
	 * 将文本解析为语法块
	 * 
	 * @param content
	 *            被解析的文本
	 * @return 语法块列表
	 */
	public static List<Block> analyze(String content) {
		content = formatText(content);
		SyntaxFilter filter = new CodePartFilter(
				new TablePartFilter(
					new HeaderOneLineFilter(
						new ListFilter(
							new HeaderNextLineFilter(null)))));

		List<Block> blocks = filter.call(content);
		return blocks;
	}

	/**
	 * 对一行文本进行语法分析，主要针对加粗，斜体等能在句中使用的格式
	 * 
	 * @param text
	 *            一行文本
	 * @return 分析结果
	 */
	public static List<ValuePart> analyzeLineText(String text) {

		List<ValuePart> result = text2ValuePart(text.trim(), new ArrayList<String>(), new ArrayList<String>());
		
		if(text.endsWith(MDToken.ROW)) {
			result.add(createValuePart("", Arrays.asList(MDToken.ROW)));
		}
		return result;
	}
	
	/**
	 * 将Text 转为 valuePart
	 * @param text 文本内容
	 * @param notCheckMDTokens 不需要检查的md token
	 * @param currentTypes 当前文本已经包含的md token类型
	 * @return valuePart 列表
	 */
	private static List<ValuePart> text2ValuePart(String text, List<String> notCheckMDTokens,
			List<String> currentTypes) {
		List<ValuePart> result = new ArrayList<ValuePart>();
		text = ValuePart.convertValue(text);
		int textLength = text.length();
		//1. 检索到第一个的md token。输出：位置i，语法：token
		int i = textLength;
		String mdToken = null;
		for (String tmp : mdTokenInLine) { // 检查是否有指定的md语法
			if (notCheckMDTokens.contains(tmp)) {
				continue;
			}
			int j = text.indexOf(tmp);
			if (j > -1 && i > j) { // 找到第一个符合要求的md语法
				i = j;
				mdToken = tmp;
			}
		}
		//2. 根据这个token检测语法是否完整
		TextLinePiece piece = checkIfCorrectSyntax(i, mdToken, text);
		//3. 对文本分为三块
		int firstPartEndIndex = textLength;
		int secondPartEndIndex = 0;
		int thirdPartEndIndex = 0;
		if(piece != null) {
			firstPartEndIndex = piece.getBeginIndex();
			secondPartEndIndex = piece.getEndIndex();
			if(secondPartEndIndex < (textLength - 1)) {
				thirdPartEndIndex = textLength;
			}
		}
		//4. 对这个token块之前的内容归档
		if(firstPartEndIndex > 0) {
			ValuePart valuePart = createValuePart(text.substring(0, firstPartEndIndex), currentTypes);
			result.add(valuePart);
		}
		//5. 对这个token块的内容进行递归分析
		if(secondPartEndIndex > 0) {
			List<String> currentTypesClone = cloneList(currentTypes);
			List<String> notCheckMDTokensClone = cloneList(notCheckMDTokens);
			notCheckMDTokensClone.add(mdToken);
			currentTypesClone.add(mdToken);
			ValuePart valuePart = null;
			
			switch (piece.getPieceType()) {
			case LINK:
				valuePart = analyzeTextInLink(piece.getTitle(), notCheckMDTokensClone, currentTypesClone);
				String tmpValue = valuePart.getTitle() + "(" + piece.getUrl() + ")";
				valuePart.setValue(tmpValue);
				valuePart.setUrl(piece.getUrl());
				result.add(valuePart);
				break;
			case IMAGE:
				valuePart = createValuePart(piece.getUrl(), currentTypesClone);
				valuePart.setTitle(piece.getTitle());
				valuePart.setUrl(piece.getUrl());
				result.add(valuePart);
				break;
			case COMMON:
			default:
				String sencondPart = text.substring(piece.getBeginIndex() + mdToken.length(), secondPartEndIndex);
				List<ValuePart> tmpList2 = text2ValuePart(sencondPart, notCheckMDTokensClone, currentTypesClone);
				for (ValuePart tmp : tmpList2) {
					result.add(tmp);
				}
				break;
			}
		}
		//6. 对这个token块之后对内容进行递归分析
		if(thirdPartEndIndex > 0) {

			String thirdPart = "";
			if (piece.getPieceType() == PieceType.IMAGE) { // image的开始符是两个字符，结束符是一个字符，所以要特殊处理
				thirdPart = text.substring(piece.getEndIndex() + 1);
			} else { // 其它标签的开始符跟结束符长度一致
				thirdPart = text.substring(piece.getEndIndex() + mdToken.length());
			}

			List<ValuePart> tmpList1 = text2ValuePart(thirdPart, notCheckMDTokens, currentTypes);
			for (ValuePart valuePart : tmpList1) {
				result.add(valuePart);
			}
		}
		
		return result;
	}
	
	private static List<String> cloneList(List<String> target) {
		List<String> result = new ArrayList<>();
		for (String tmp : target) {
			result.add(tmp);
		}
		return result;
	}
	
	/**
	 * 检查mdtoken 对应的语法是否完整
	 * @param i 开始查找的位置
	 * @param mdToken 查找的md token
	 * @param text 被查找的文本
	 * @return mdtoken对应的语法块，如果找不到，则返回null
	 */
	private static TextLinePiece checkIfCorrectSyntax(int i, String mdToken, String text) {
		if(mdToken == null) {
			return null;
		}
		TextLinePiece textLinePiece = null;
		if (mdToken.equals(MDToken.LINK) || mdToken.equals(MDToken.IMG)) {
			textLinePiece = hasLinkOrImage(text, mdToken.equals(MDToken.LINK));
		} else {
			int j = text.indexOf(mdToken, i + mdToken.length());
			if(j > -1) {
				textLinePiece = new TextLinePiece(i, j, PieceType.COMMON);
			}
		}
		return textLinePiece;
	}

	private static ValuePart createValuePart(String value, List<String> mdTokens) {
		ValuePart valuePart = new ValuePart();
		valuePart.setValue(value);
		if (mdTokens.size() > 0) {
			BlockType[] types = new BlockType[mdTokens.size()];
			int i = 0;
			for (int k = (mdTokens.size() - 1); k >= 0; k--) {
				types[i] = MDToken.convert(mdTokens.get(k)); // 这里引入i，是为了数组反序
				i++;
			}
			valuePart.setTypes(types);
		}

		return valuePart;
	}
	
	private static TextLinePiece hasLinkOrImage(String str, boolean isLink) {
		TextLinePiece linkOrImageBean = new TextLinePiece();
		linkOrImageBean.setPieceType(isLink?PieceType.LINK:PieceType.IMAGE);
		String token = null;
		if (isLink) {
			token = MDToken.LINK;
		} else {
			token = MDToken.IMG;
		}
		int i = str.indexOf(token);
		int j = str.indexOf("]", i);
		if (j > 0) {
			int k = str.indexOf("(", j);
			if (k > 0 && k == (j + 1)) {
				int l = str.indexOf(")", k);
				if (l > 0) {
					String strHasUrl = str.substring(k + 1, l).trim();
					int m = strHasUrl.indexOf(" ");
					String url = "";
					if (m > -1) {
						url = strHasUrl.substring(0, m);
					} else {
						url = strHasUrl;
					}
					String title = str.substring(i + token.length(), j);
					linkOrImageBean.setBeginIndex(i);
					linkOrImageBean.setEndIndex(l);
					linkOrImageBean.setTitle(title);
					linkOrImageBean.setUrl(url);
					return linkOrImageBean;
				}
			}
		}
		return null;
	}

	private static ValuePart analyzeTextInLink(String str, List<String> notCheckMDTokens, List<String> currentTypes) {
		String mdToken = null;
		for (String tmp : mdTokenInLine) { // 检查是否有指定的md语法
			if (notCheckMDTokens.contains(tmp)) {
				continue;
			}
			if (str.startsWith(tmp)) {
				int end = str.indexOf(tmp, tmp.length());
				if (end > 0) {
					mdToken = tmp;
					break;
				}
			}
		}
		if (mdToken != null) {
			notCheckMDTokens.add(mdToken);
			currentTypes.add(mdToken);
			str = str.substring(mdToken.length(), str.length() - mdToken.length());
			return analyzeTextInLink(str, notCheckMDTokens, currentTypes);
		} else {
			ValuePart valuePart = createValuePart("", currentTypes);
			valuePart.setTitle(str);
			return valuePart;
		}
	}
	
	/**
	 * 内容格式化
	 * @param text 需要格式化的内容
	 * @return 结果
	 */
	private static String formatText(String text) {
		text = text.replaceAll("\t", "    ");
		return text;
	}
}
