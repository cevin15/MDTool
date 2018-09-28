package com.youbenzi.mdtool.export;

import java.util.List;

import com.youbenzi.mdtool.markdown.bean.Block;

public interface Decorator {
	
	public void beginWork(String outputFilePath);
	
	public void decorate(List<Block> list);
	
	public void afterWork(String outputFilePath);
	
}
