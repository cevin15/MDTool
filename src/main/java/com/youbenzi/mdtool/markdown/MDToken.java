package com.youbenzi.mdtool.markdown;

import java.util.LinkedHashMap;
import java.util.Map;

public class MDToken {

	public static final String QUOTE = ">";
	public static final String CODE = "```";
	public static final String CODE_BLANK = "    ";
	public static final String HEADLINE = "#";
	public static final String IMG = "![";
	public static final String LINK = "[";
	public static final String UNORDERED_LIST1 = "* ";
	public static final String UNORDERED_LIST2 = "- ";
	public static final String UNORDERED_LIST3 = "+ ";
	public static final String TODO_LIST_UNCHECKED = "[ ]";
	public static final String TODO_LIST_CHECKED = "[x]";
	public static final String BOLD_WORD1 = "**";
	public static final String BOLD_WORD2 = "__";
	public static final String ITALIC_WORD = "_";
	public static final String ITALIC_WORD_2 = "*";
	public static final String STRIKE_WORD = "~~";
	public static final String CODE_WORD = "`";
	public static final String ROW = "  ";
	public static final String TABLE_COL = "|";
	public static final String CUSTOM_BLANK_CHAR = "@br";

	public static final Map<String, String> PLACEHOLDER_MAP = new LinkedHashMap<String, String>() { // 需要显示的特殊符号的占位符
		private static final long serialVersionUID = 5649442662460683378L;
		{
			put("\\\\", "$BACKSLASH");
			put("\\" + IMG, "$IMG");
			put("\\" + LINK, "$LINK");
			// put("\\" + BOLD_WORD, "$BOLDWORD"); //因为有 ITALIC_WORD_2 的存在，所以不需要这个
			put("\\" + ITALIC_WORD, "$ITALICWORD");
			put("\\" + ITALIC_WORD_2, "$2ITALICWORD");
			put("\\" + STRIKE_WORD, "$STRIKEWORD");
			put("\\" + CODE_WORD, "$CODEWORD");
			put("\\" + TABLE_COL, "$TABLE_COL");
			put("\\", "");
		}
	};

	public static BlockType convert(String mdToken) {
		if (mdToken.equals(QUOTE)) {
			return BlockType.QUOTE;
		} else if (mdToken.equals(CODE)) {
			return BlockType.CODE;
		} else if (mdToken.equals(HEADLINE)) {
			return BlockType.HEADLINE;
		} else if (mdToken.equals(IMG)) {
			return BlockType.IMG;
		} else if (mdToken.equals(BOLD_WORD1) || mdToken.equals(BOLD_WORD2)) {
			return BlockType.BOLD_WORD;
		} else if (mdToken.equals(ITALIC_WORD) || mdToken.equals(ITALIC_WORD_2)) {
			return BlockType.ITALIC_WORD;
		} else if (mdToken.equals(STRIKE_WORD)) {
			return BlockType.STRIKE_WORD;
		} else if (mdToken.equals(CODE_WORD)) {
			return BlockType.CODE_WORD;
		} else if (mdToken.equals(LINK)) {
			return BlockType.LINK;
		} else if (mdToken.equals(ROW)) {
			return BlockType.ROW;
		} else {
			return BlockType.NONE;
		}
	}
}
