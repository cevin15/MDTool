package com.youbenzi.mdtool.markdown.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.TableCellAlign;
import com.youbenzi.mdtool.markdown.bean.Block;

public class TableBuilder implements BlockBuilder{

	private List<List<String>> data;
	private Map<Integer, TableCellAlign> cellAligns;
	
	public TableBuilder(List<List<String>> data, Map<Integer, TableCellAlign> cellAligns) {
		super();
		this.data = data;
		this.cellAligns = cellAligns;
	}

	@Override
	public Block bulid() {
		Block block = new Block();
		block.setType(BlockType.TABLE);
		block.setTableData(convertData(data, cellAligns));
		return block;
	}

	private List<List<Block>> convertData(List<List<String>> data, Map<Integer, TableCellAlign> cellAligns) {
		List<List<Block>> result = new ArrayList<>();
		for (List<String> list : data) {
			List<Block> blocks = new ArrayList<>();
			for (int i = 0, s = list.size(); i < s; i ++) {
				String str = list.get(i);
				Block block = new CommonTextBuilder(str).bulid();
				TableCellAlign align = cellAligns.get(i);
				block.setAlign(align == null?TableCellAlign.NONE:align);
				blocks.add(block);
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
