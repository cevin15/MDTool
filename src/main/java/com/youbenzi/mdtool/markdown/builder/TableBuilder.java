package com.youbenzi.mdtool.markdown.builder;

import java.util.ArrayList;
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

		
		block.setTableData(convertData(data));
		return block;
	}

	private List<List<Block>> convertData(List<List<String>> data) {
		List<List<Block>> result = new ArrayList<>();
		for (List<String> list : data) {
			List<Block> blocks = new ArrayList<>();
			for (String str : list) {
				blocks.add(new CommonTextBuilder(str).bulid());
			}
			result.add(blocks);
		}
		return result;
	}
	
	@Override
	public boolean isRightType() {
		return false;
	}

}
