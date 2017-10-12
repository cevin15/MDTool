package com.youbenzi.mdtool.markdown.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.youbenzi.mdtool.markdown.Analyzer;
import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.ValuePart;

/**
 * 列表（有序列表，无序列表，引用）builder
 * 
 * @author yangyingqiang 2017年7月11日 下午6:38:05
 */
public class MultiListBuilder implements BlockBuilder {

	private String content;

	public MultiListBuilder(String content) {
		this.content = content;
	}

	/**
	 * 找出当前行的缩进空格数
	 * 
	 * @param line
	 * @return
	 */
	private String blankStrInHead(String line) {
		if (line == null) {
			return "";
		}
		String blankStr = " ";
		while (line.startsWith(blankStr)) {
			blankStr = blankStr + " ";
		}
		return blankStr.substring(1, blankStr.length());
	}

	public Block bulid() {

		BufferedReader br = new BufferedReader(new StringReader(content));
		Block block = new Block();
		List<Block> listData = new ArrayList<Block>();
		block.setType(BlockType.LIST);
		block.setListData(listData);
		try {
			String value = br.readLine();
			while (value != null) {
				Block subBlock = new Block();
				value = buildListBlock(subBlock, value, br, new ArrayList<TypeAndBlank>());
				listData.add(subBlock);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return block;
	}

	public class TypeAndBlank {

		private String blankStr;
		private BlockType blockType;

		public TypeAndBlank(String blankStr, BlockType blockType) {
			super();
			this.blankStr = blankStr;
			this.blockType = blockType;
		}

		public String getBlankStr() {
			return blankStr;
		}

		public BlockType getBlockType() {
			return blockType;
		}
	}

	private boolean jumpOut(List<TypeAndBlank> levels, TypeAndBlank currentLevels) {
		ArrayList<TypeAndBlank> childLevels = new ArrayList<MultiListBuilder.TypeAndBlank>();

		for (TypeAndBlank typeAndBlank : levels) {
			if (typeAndBlank.getBlankStr().equals(currentLevels.getBlankStr())
					&& typeAndBlank.getBlockType() == currentLevels.getBlockType()) {
				Iterator<TypeAndBlank> it = levels.iterator();
				for (int i = 0, l = childLevels.size(); i < l; i++) {
					it.next();
					it.remove();
				}
				return true;
			} else {
				childLevels.add(typeAndBlank);
			}
		}
		return false;
	}

	private String buildListBlock(Block result, String value, BufferedReader br, List<TypeAndBlank> levels)
			throws IOException {

		String firstBlankStr = blankStrInHead(value);
		BlockType firstCurrentType = listType(value);
		List<Block> listData = new ArrayList<Block>();

		result.setType(firstCurrentType);
		result.setListData(listData);
		levels.add(0, new TypeAndBlank(firstBlankStr, firstCurrentType));
		while (value != null) {
			BlockType blockType = listType(value);
			String blankStr = blankStrInHead(value);
			if (!blankStr.equals(firstBlankStr) || blockType != firstCurrentType) { // 下一行格式跟当前行不一致，跳出while
				return value;
			}
			Block block = new Block();
			value = value.substring(firstBlankStr.length()); // 删除缩进空格

			int index = computeCharIndex(value, blockType);
			if (index < 0) {
				value = br.readLine();
				continue;
			}
			value = value.substring(index + 1).trim(); // 取出当前行取出列表符之后的真正数据

			if (value.equals("")) { // 空行直接忽略
				value = br.readLine();
				continue;
			}
			int i = 0;
			if (value.startsWith(MDToken.HEADLINE)) { // 检查是否有标题格式
				i = value.lastIndexOf(MDToken.HEADLINE);
			}
			if (i > 0) {
				value = value.substring(i + 1).trim();
			}

			List<ValuePart> list = Analyzer.analyzeLineText(value);
			if (i > 0) {
				for (ValuePart valuePart : list) {
					valuePart.addType(BlockType.HEADLINE);
					valuePart.setLevel(i);
				}
			}
			block.setValueParts(list);
			listData.add(block);
			value = br.readLine();
			if (value == null) {
				break;
			}
			blockType = listType(value);
			blankStr = blankStrInHead(value);
			if (value != null) {
				if (blankStr.equals(firstBlankStr) && blockType != firstCurrentType) { // 同级别的列表，但是不同格式，跳出while
					return value;
				}
				if (!blankStr.equals(firstBlankStr) || blockType != firstCurrentType) { // 下一行格式跟当前行不一致
					if (jumpOut(levels, new TypeAndBlank(blankStr, blockType))) { // 检查是否为父级列表，是的话跳出while，否则作为子列表
						return value;
					}
					value = buildListBlock(block, value, br, levels);
				}
			}
		}
		return value;
	}

	private static BlockType listType(String line) {
		if (line == null) {
			return null;
		}
		if (isOrderedList(line)) {
			return BlockType.ORDERED_LIST;
		}
		if (isUnOrderedList(line)) {
			return BlockType.UNORDERED_LIST;
		}
		if (isQuote(line)) {
			return BlockType.QUOTE;
		}
		return null;
	}

	private static int computeCharIndex(String line, BlockType type) {
		if (type == BlockType.ORDERED_LIST || type == BlockType.UNORDERED_LIST) {
			return line.indexOf(" ");
		}
		if (type == BlockType.QUOTE) {
			return line.indexOf(MDToken.QUOTE);
		}
		return -1;
	}

	public static boolean isList(String str) {
		return isOrderedList(str) || isUnOrderedList(str) || isQuote(str);
	}

	public static boolean isOrderedList(String str) {
		return Pattern.matches("^[\\d]+\\. [\\d\\D][^\n]*$", str.trim());
	}

	public static boolean isUnOrderedList(String str) {
		return str.trim().startsWith(MDToken.UNORDERED_LIST1) || str.trim().startsWith(MDToken.UNORDERED_LIST2);
	}

	public static boolean isQuote(String str) {
		return str.trim().startsWith(MDToken.QUOTE);
	}

	public boolean isRightType() {
		return false;
	}

}
