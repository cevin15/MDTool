package com.youbenzi.mdtool.markdown.bean;

import java.util.Arrays;

import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.tool.Tools;

public class ValuePart{
	private String value;
	private BlockType[] types;
	private int level;
	private String url;
	private String title;
	
	public ValuePart(){
		super();
	}
	
	public ValuePart(String value){
		this.value = Tools.revertValue(value);
	}
	
	public ValuePart(String value, BlockType... types){
		this.value = Tools.revertValue(value);
		this.types = types;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = Tools.revertValue(value);
	}
	public BlockType[] getTypes() {
		return types;
	}
	public void setTypes(BlockType... types) {
		this.types = types;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void addType(BlockType type){
		if(types!=null){
			types = Arrays.copyOf(types, types.length+1);
		}else{
			types = new BlockType[1];
		}
		
		types[types.length-1] = type;
	}

	@Override
	public String toString() {
		return "value:"+value+"|types:"+Arrays.toString(types);
	}
	
	public static void main(String[] args) {
		ValuePart part = new ValuePart();
		part.setTypes(BlockType.BOLD_WORD, BlockType.CODE);
		System.out.println(Arrays.toString(part.types));
		part.addType(BlockType.HEADLINE);
		System.out.println(Arrays.toString(part.types));
	}
}