package com.youbenzi.mdtool.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.ValuePart;
import com.youbenzi.mdtool.tool.MDTool;
import com.youbenzi.mdtool.tool.Tools;


public class HTMLDecorator implements Decorator{

	private StringBuilder content = new StringBuilder();
	
	public void beginWork(String outputFilePath) {
		
	}
	public void decorate(List<Block> list) {
		for (Block block : list) {
			try{
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
					str = commonTextParagraph(block.getValueParts());
					break;
				}

				content.append(str);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void afterWork(String outputFilePath) {
		File file =  new File(outputFilePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(writer!=null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String outputHtml(){
		return content.toString();
	}
	
	private String codeParagraph(ValuePart[] valueParts){

		String value = valueParts[0].getValue();
		StringBuilder tmp = new StringBuilder("<pre>\n");
		tmp.append("<code>");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		if(value.endsWith("\n")) {
			value = value.substring(0, value.length() - "\n".length());
		}
		tmp.append(value);
		tmp.append("</code>\n");
		tmp.append("</pre>\n");
		
		return tmp.toString();
	}
	
	private String headerParagraph(ValuePart[] valueParts, int level){
		level = level + 1;
		StringBuilder tmp = new StringBuilder("<h"+level+">");
		
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			String value  = valuePart.getValue();
			if(types!=null){
				for (BlockType type : types) {
					value = formatByType(type, value, valuePart);
				}
			}
			tmp.append(value);
		}
		tmp.append("</h"+level+">\n");
		return tmp.toString();
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
			default:
				break;
		}
		return tmp.toString();
	}
	
	private String formatByType(BlockType type, String value, ValuePart valuePart){
		switch (type) {
			case BOLD_WORD:
				return "<strong>"+value+"</strong>";
			case ITALIC_WORD:
				return "<em>"+value+"</em>";
			case STRIKE_WORD:
				return "<del>"+value+"</del>";
			case CODE_WORD:
				return "<code>"+value+"</code>";
			case HEADLINE:
				return "<h"+valuePart.getLevel()+">"+value+"</h"+valuePart.getLevel()+">";
			case LINK:
				return "<a href=\""+valuePart.getUrl()+"\" title=\"" + Tools.filterHtml(value) + "\">"+value+"</a>";
			case IMG:
				return "<img src=\"" + valuePart.getUrl() + "\" title=\"" 
					+ valuePart.getTitle() + "\" alt=\""+ valuePart.getTitle() +"\" />";
			case ROW:
				return "<br/>";
			default:
				return value;
		}
	}
	
	private String tableParagraph(List<List<String>> tableData){
		
		int nRows = tableData.size();
    	int nCols = 0;
    	for (List<String> list : tableData) {
			int s = list.size();
			if(nCols<s){
				nCols = s;
			}
		}
    	StringBuilder tmp = new StringBuilder("<table>\n");
    	
        for (int i=0; i<nRows; i++) {
			tmp.append("<tr>\n");
			List<String> colDatas = tableData.get(i);
			for(int j=0; j<nCols; j++){
				
				if(i==0){
					tmp.append("<th>");
				}else{
					tmp.append("<td>");
				}
				tmp.append("<p>");
				try {
					tmp.append(colDatas.get(j));
				} catch (Exception e) {
					tmp.append("");
				}
				tmp.append("</p>");
				if(i==0){
					tmp.append("</th>\n");
				}else{
					tmp.append("</td>\n");
				}
			}
			tmp.append("</tr>\n");
		}
        tmp.append("</table>\n");
        return tmp.toString();
    }
	
	private String abstractListParagraph(List<Block> listData, String parentTag, String childTag){
		StringBuilder tmp = new StringBuilder("<" + parentTag + ">\n");
		for (final Block block : listData) {
			tmp.append(listLine(block.getValueParts(), childTag, new LineHelper() {
				public String subList() {
					if(block.getType() != null) {
						return listParagraph(block.getType(), block.getListData());
					} else {
						return "";
					}
				}
			}));
		}
		tmp.append("</" + parentTag + ">\n");
		
		return tmp.toString();
	}
	
	private String quoteParagraph(List<Block> listData){
		return abstractListParagraph(listData, "blockquote", "p");
	}
	
	private String unorderedListParagraph(List<Block> listData){
		return abstractListParagraph(listData, "ul", "li");
	}
	
	private String orderedListParagraph(List<Block> listData){
		String result = abstractListParagraph(listData, "ol", "li");
		return result;
	}
	
	private String commonTextParagraph(ValuePart[] valueParts){
		
		return listLine(valueParts, "p", new LineHelper() {
			public String subList() {
				return "";
			}
		});
	}
	
	private String listLine(ValuePart[] valueParts, String tag, LineHelper lineHelper){
		StringBuilder tmp = new StringBuilder();
		if(valueParts==null){
			return tmp.toString();
		}
		tmp.append("<"+tag+">");
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			
			String value = valuePart.getValue();
			if(hasLink(types)){
				value = valuePart.getTitle();
			}
			if(types!=null){
				for (BlockType type : types) {
					value = formatByType(type, value, valuePart);
				}
			}
			tmp.append(value);
			
		}
		tmp.append(lineHelper.subList());
		tmp.append("</"+tag+">\n");
		return tmp.toString();
	}
	
	private boolean hasLink(BlockType[] types){
		if(types==null){
			return false;
		}
		for (BlockType blockType : types) {
			if(blockType==BlockType.LINK){
				return true;
			}
		}
		return false;
	}
	
	public interface LineHelper {
		public String subList();
	}
	


	public static void main(String[] args) {
		String content = "1. 列表1.1\n"
						+"2. 列表1.2\n"
						+"    * 列表2.1\n"
						+"    * 列表2.2\n"
						+"    * 列表2.3\n"
						+"    * 列表2.4\n";
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
