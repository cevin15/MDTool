package com.youbenzi.mdtool.markdown.builder;

import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.bean.Block;
import com.youbenzi.mdtool.markdown.bean.ValuePart;

public class CodeBuilder implements BlockBuilder{

	private String content;
	public CodeBuilder(String content){
		this.content = content;
	}
	
	public Block bulid() {
		Block block = new Block();
		block.setType(BlockType.CODE);
		block.setValueParts(new ValuePart(content));
		return block;
	}

	public boolean isRightType() {
		return false;
	}

}
