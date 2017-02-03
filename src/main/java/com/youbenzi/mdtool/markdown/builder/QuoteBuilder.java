package com.youbenzi.mdtool.markdown.builder;

import com.youbenzi.mdtool.markdown.BlockType;
import com.youbenzi.mdtool.markdown.MDToken;

public class QuoteBuilder extends ListBuilder{

	public QuoteBuilder(String content){
		super(content, BlockType.QUOTE);
	}

	@Override
	public int computeCharIndex(String str) {
		return str.indexOf(MDToken.QUOTE);
	}
}
