package com.youbenzi.mdtool.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.TableCellAlign;
import com.youbenzi.mdtool.markdown.bean.Block;
import com.youbenzi.mdtool.markdown.bean.ValuePart;
import com.youbenzi.mdtool.tool.MDTool;
import com.youbenzi.mdtool.tool.Tools;

public class HTMLDecorator implements Decorator {

	private StringBuilder content = new StringBuilder();

	public void beginWork(String outputFilePath) {

	}

	public void decorate(List<Block> list) {
		for (Block block : list) {
			try {
				String str;
				switch (block.getType()) {
				case CODE:
					str = codeParagraph(block.getValueParts());
					break;
				case HEADLINE:
					str = headerParagraph(block.getValueParts(), block.getLevel());
					break;
				case QUOTE:
					str = quoteParagraph(block.getListData());
					break;
				case TABLE:
					str = tableParagraph(block.getTableData());
					break;
				case LIST:
					str = listParagraph(block.getListData());
					break;
				default:
					str = commonTextParagraph(block.getValueParts(), true);
					break;
				}

				content.append(str).append("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void afterWork(String outputFilePath) {
		File file = new File(outputFilePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String outputHtml() {
		return content.toString();
	}

	private String codeParagraph(ValuePart[] valueParts) {

		String value = valueParts[0].getValue();
		StringBuilder tmp = new StringBuilder("<pre>");
		tmp.append("<code>");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		if (value.endsWith("\n")) {
			value = value.substring(0, value.length() - "\n".length());
		}
		tmp.append(value);
		tmp.append("</code>");
		tmp.append("</pre>\n");

		return tmp.toString();
	}

	private String headerParagraph(ValuePart[] valueParts, int level) {
		return oneLineHtml(valueParts, "h" + level);
	}

	private String listParagraph(List<Block> listData) {
		StringBuilder tmp = new StringBuilder();
		for (Block block : listData) {
			tmp.append(listParagraph(block.getType(), block.getListData()));
		}
		return tmp.toString();
	}

	private String listParagraph(BlockType blockType, List<Block> listData) {
		StringBuilder tmp = new StringBuilder();

		switch (blockType) {
		case ORDERED_LIST:
			tmp.append(orderedListParagraph(listData)).append("\n");
			break;
		case UNORDERED_LIST:
			tmp.append(unorderedListParagraph(listData)).append("\n");
			break;
		case QUOTE:
			tmp.append(quoteParagraph(listData)).append("\n");
			break;
		case TODO_LIST:
			tmp.append(todoListParagraph(listData)).append("\n");
			break;
		default:
			break;
		}
		return tmp.toString();
	}

	private String formatByType(BlockType type, String value, ValuePart valuePart) {
		switch (type) {
		case BOLD_WORD:
			return "<strong>" + value + "</strong>";
		case ITALIC_WORD:
			return "<em>" + value + "</em>";
		case STRIKE_WORD:
			return "<del>" + value + "</del>";
		case CODE_WORD:
			return "<code>" + value + "</code>";
		case HEADLINE:
			int level = valuePart.getLevel() + 1;
			return "<h" + level + ">" + value + "</h" + level + ">";
		case LINK:
			return "<a href=\"" + valuePart.getUrl() + "\" title=\"" + Tools.filterHtml(value) + "\">" + value + "</a>";
		case IMG:
			return "<img src=\"" + valuePart.getUrl() + "\" title=\"" + valuePart.getTitle() + "\" alt=\""
					+ valuePart.getTitle() + "\" />";
		case ROW:
			return "<br/>";
		default:
			return value;
		}
	}

	private String tableParagraph(List<List<Block>> tableData) {

		int nRows = tableData.size();
		int nCols = 0;
		for (List<Block> list : tableData) {
			int s = list.size();
			if (nCols < s) {
				nCols = s;
			}
		}
		StringBuilder tmp = new StringBuilder("<table>\n");

		for (int i = 0; i < nRows; i++) {
			tmp.append("<tr>\n");
			List<Block> colDatas = tableData.get(i);
			for (int j = 0; j < nCols; j++) {
				Block block = colDatas.get(j);
				boolean head = (i == 0);
				tmp.append(buildColBegin(head, block.getAlign()));
				try {
					tmp.append(commonTextParagraph(colDatas.get(j).getValueParts(), false));
				} catch (Exception e) {
					tmp.append("");
				}
				tmp.append("</" + (head?"th":"td") + ">\n");
			}
			tmp.append("</tr>\n");
		}
		tmp.append("</table>\n");
		return tmp.toString();
	}
	
	private String buildColBegin(boolean head, TableCellAlign align) {
		String alignString = "";
		if (align != TableCellAlign.NONE) {
			alignString = "align=\"" + align.value() + "\"";
		}
		return "<" + (head?"th":"td") + " " + alignString + ">";
	}

	private String abstractListParagraph(List<Block> listData, LineHelper lineHelper) {
		StringBuilder tmp = new StringBuilder(lineHelper.parentTagBegin() + "\n");
		for (final Block block : listData) {
			Block lineBlock = block.getLineData();
			String content;
			if (lineBlock.getType() == BlockType.HEADLINE) {
				content = headerParagraph(lineBlock.getValueParts(), lineBlock.getLevel());
			} else {
				content = commonTextParagraph(lineBlock.getValueParts(), lineHelper.needDefaultChild());
			}
			tmp.append(lineHelper.childTagBegin(block));
			tmp.append(content).append("\n").append(lineHelper.subList(block));
			tmp.append(lineHelper.childTagEnd());
		}
		tmp.append(lineHelper.parentTagEnd() + "\n");

		return tmp.toString();
	}

	private String quoteParagraph(List<Block> listData) {
		return abstractListParagraph(listData, new DefaultLineHelper("blockquote"));
	}

	private String unorderedListParagraph(List<Block> listData) {
		return abstractListParagraph(listData, new DefaultLineHelper("ul", "li"));
	}

	private String orderedListParagraph(List<Block> listData) {
		return abstractListParagraph(listData, new DefaultLineHelper("ol", "li"));
	}

	private String todoListParagraph(List<Block> listData) {
		return abstractListParagraph(listData, new DefaultLineHelper("ul", "li") {
			@Override
			public String decorate(Block block, String tag) {
				if (block.getMdToken().equals(MDToken.TODO_LIST_UNCHECKED)) {
					return "<" + tag + "><i class=\"unchecked_icon\"></i>";
				} else {
					return "<" + tag + "><i class=\"checked_icon\"></i>";
				}
			}
		});
	}

	private String commonTextParagraph(ValuePart[] valueParts, boolean needPTag) {
		return oneLineHtml(valueParts, needPTag ? "p" : null);
	}

	private String oneLineHtml(ValuePart[] valueParts, String tagName) {
		StringBuilder result = new StringBuilder();
		if (tagName != null && !tagName.trim().equals("")) {
			result.append("<" + tagName + ">");
		}
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();

			String value = valuePart.getValue();
			if (hasLink(types)) {
				value = valuePart.getTitle();
			}
			if (types != null) {
				for (BlockType type : types) {
					value = formatByType(type, value, valuePart);
				}
			}
			result.append(value);
		}
		if (tagName != null && !tagName.trim().equals("")) {
			result.append("</" + tagName + ">");
		}
		return result.toString();
	}

	private boolean hasLink(BlockType[] types) {
		if (types == null) {
			return false;
		}
		for (BlockType blockType : types) {
			if (blockType == BlockType.LINK) {
				return true;
			}
		}
		return false;
	}

	public abstract class LineHelper {
		public abstract String subList(Block block);

		public abstract String parentTagBegin();

		public abstract String parentTagEnd();

		public abstract String childTagBegin(Block block);

		public abstract String childTagEnd();

		public abstract boolean needDefaultChild();

		public String decorate(Block block, String tag) {
			if (tag == null) {
				return "";
			}
			return "<" + tag + ">";
		}
	}

	public class DefaultLineHelper extends LineHelper {
		private String parentTag;
		private String childTag;

		public DefaultLineHelper(String parentTag, String childTag) {
			this.parentTag = parentTag;
			this.childTag = childTag;
		}

		public DefaultLineHelper(String parentTag) {
			this.parentTag = parentTag;
			this.childTag = null;
		}

		public boolean needDefaultChild() {
			return childTag == null;
		}

		public String parentTagBegin() {
			return "<" + parentTag + ">";
		}

		public String parentTagEnd() {
			return "</" + parentTag + ">";
		}

		public String childTagBegin(Block block) {
			return decorate(block, childTag);
		}

		public String childTagEnd() {
			if (childTag == null) {
				return "";
			}
			return "</" + childTag + ">";
		}

		public String subList(Block block) {
			if (block.getType() != null) {
				return listParagraph(block.getType(), block.getListData());
			} else {
				return "";
			}
		}
	}

	public static void main(String[] args) {
		String content = "1. 列表1.1\n" + "2. 列表1.2\n" + "    * 列表2.1\n" + "    * 列表2.2\n" + "    * 列表2.3\n"
				+ "    * 列表2.4\n";
//						+"3. 列表1.3\n"
//						+"    * 列表2.1\n"
//						+"    * 列表2.2\n"
//						+"    * 列表2.3\n"
//						+"    * 列表2.4\n"
//						+"        * 列表3.1\n"
//						+"        * 列表3.2\n"
//						+"        * 列表3.3\n"
//						+"        * 列表3.4\n"
//						+"4. 列表1.4\n"
//						+"* 列表1.4\n"
//						+"* 列表1.4\n"
//						+"* 列表1.4\n";
		System.out.println(MDTool.markdown2Html(content));
	}
}
