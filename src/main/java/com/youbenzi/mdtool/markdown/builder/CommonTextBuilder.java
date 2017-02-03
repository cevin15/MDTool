package com.youbenzi.mdtool.markdown.builder;

import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.MDAnalyzer;
import com.youbenzi.mdtool.markdown.ValuePart;

public class CommonTextBuilder implements BlockBuilder{

	private String content;
	public CommonTextBuilder(String content){
		this.content = content;
	}
	
	public Block bulid() {
		Block block = new Block();
		
		block.setType(BlockType.NONE);
		List<ValuePart> list = MDAnalyzer.analyzeTextLine(content);
		block.setValueParts(list);
		
		return block;
	}

	public boolean isRightType() {
		return true;
	}

}
