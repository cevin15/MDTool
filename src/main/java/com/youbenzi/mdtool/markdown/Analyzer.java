package com.youbenzi.mdtool.markdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.youbenzi.mdtool.markdown.filter.CodeListFilter;
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
		SyntaxFilter filter = new CodePartFilter(new CodeListFilter(
				new TablePartFilter(new HeaderOneLineFilter(new ListFilter(new HeaderNextLineFilter(null))))));

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
	public static List<ValuePart> analyzeTextLine(String text) {
		List<ValuePart> result = analyzeTextLine(text.trim(), new ArrayList<String>(), new ArrayList<String>());
		if(text.endsWith(MDToken.ROW)) {
			result.add(createValuePart("", Arrays.asList(MDToken.ROW)));
		}
		return result;
	}

	
	/**
	 * 对一行文本进行语法分析，主要针对加粗，斜体等能在句中使用的格式
	 * 
	 * @param text
	 *            一行文本
	 * @param notCheckMDTokens
	 *            已经检查过的md语法
	 * @param currentTypes
	 *            当前文本已有的语法
	 * @return 分析结果
	 */
	private static List<ValuePart> analyzeTextLine(String text, List<String> notCheckMDTokens,
			List<String> currentTypes) {
		List<ValuePart> result = new ArrayList<ValuePart>();
		if (text == null || text.length() < 0) {
			return result;
		}
		int i = text.length();
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
		if (mdToken != null) { // 有指定的md语法
			LinkOrImageBeanTmp linkOrImageBeanTmp = null;
			int j = -1;
			if (mdToken.equals(MDToken.LINK) || mdToken.equals(MDToken.IMG)) {
				linkOrImageBeanTmp = hasLinkOrImage(text, mdToken.equals(MDToken.LINK));
				if (linkOrImageBeanTmp != null) {
					j = linkOrImageBeanTmp.getEndIndex();
				}
			} else {
				j = text.indexOf(mdToken, i + mdToken.length());
			}
			if (j > -1) { // 该语法完整
				if (i > 0) {
					String v1 = text.substring(0, i);
					ValuePart valuePart = createValuePart(v1, currentTypes);
					result.add(valuePart);
				}

				notCheckMDTokens.add(mdToken);
				currentTypes.add(mdToken);
				if (linkOrImageBeanTmp != null) {
					List<String> ct4Link = new ArrayList<String>();
					for (String type : currentTypes) {
						ct4Link.add(type);
					}
					ValuePart valuePart = null;
					if (linkOrImageBeanTmp.isLink()) {
						valuePart = analyzeTextInLink(linkOrImageBeanTmp.getTitle(), notCheckMDTokens, ct4Link);
						String tmpValue = valuePart.getTitle() + "(" + linkOrImageBeanTmp.getUrl() + ")";
						valuePart.setValue(tmpValue);
					} else {
						valuePart = createValuePart(linkOrImageBeanTmp.getUrl(), ct4Link);
						valuePart.setTitle(linkOrImageBeanTmp.getTitle());
					}
					valuePart.setUrl(linkOrImageBeanTmp.getUrl());
					result.add(valuePart);
				} else {
					String v2 = text.substring(i + mdToken.length(), j);
					List<ValuePart> tmpList2 = analyzeTextLine(v2, notCheckMDTokens, currentTypes);
					for (ValuePart valuePart : tmpList2) {
						result.add(valuePart);
					}
				}
				String v3 = "";
				if (mdToken.equals(MDToken.IMG)) { // image的开始符是两个字符，结束符是一个字符，所以要特殊处理
					v3 = text.substring(j + 1);
				} else { // 其它标签的开始符跟结束符长度一致
					v3 = text.substring(j + mdToken.length());
				}

				notCheckMDTokens.remove(notCheckMDTokens.size() - 1);
				currentTypes.remove(currentTypes.size() - 1);
				List<ValuePart> tmpList1 = analyzeTextLine(v3, notCheckMDTokens, currentTypes);
				for (ValuePart valuePart : tmpList1) {
					result.add(valuePart);
				}
			} else { // 该语法不完整，没结束符
				notCheckMDTokens.add(mdToken);
				List<ValuePart> tmpList = analyzeTextLine(text, notCheckMDTokens, currentTypes);
				for (ValuePart valuePart : tmpList) {
					result.add(valuePart);
				}
			}
		} else { // 没有指定的md语法
			if (text != null && text.length() > 0) {
				ValuePart valuePart = createValuePart(text, currentTypes);
				result.add(valuePart);
			}
		}
		return result;
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

	private static LinkOrImageBeanTmp hasLinkOrImage(String str, boolean isLink) {
		LinkOrImageBeanTmp linkOrImageBean = new LinkOrImageBeanTmp();
		linkOrImageBean.setLink(isLink);
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
}
