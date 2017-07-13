package com.youbenzi.mdtool.markdown.builder;

import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.BlockType;

public class TableBuilder implements BlockBuilder{

	private List<List<String>> data;
	
	public TableBuilder(List<List<String>> data) {
		super();
		this.data = data;
	}

	@Override
	public Block bulid() {
		Block block = new Block();
		block.setType(BlockType.TABLE);
		block.setTableData(data);
		return block;
	}

	@Override
	public boolean isRightType() {
		return false;
	}

}
