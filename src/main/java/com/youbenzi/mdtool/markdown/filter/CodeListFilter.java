package com.youbenzi.mdtool.markdown.filter;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.MDToken;
import com.youbenzi.mdtool.markdown.builder.CodeBuilder;

/**
 * 筛选出内容中的代码（四个空格格式）
 * @author yangyingqiang
 * 2017年7月14日 上午11:43:07
 */
public class CodeListFilter extends ListFilter{

	public CodeListFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	protected boolean isListLine(String target) {
		return target.startsWith(MDToken.CODE_BLANK);
	}
	
	@Override
	protected String trim(String target) {
		return target.substring(MDToken.CODE_BLANK.length());
	}
	
	@Override
	protected Block buildBlock(String source) {
		return new CodeBuilder(source.toString()).bulid();
	}
	
	@Override
	protected void how2AppendIfBlank(StringBuilder target) {
		target.append("\n");
	}
}
