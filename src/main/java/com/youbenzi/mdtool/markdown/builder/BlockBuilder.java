package com.youbenzi.mdtool.markdown.builder;

import com.youbenzi.mdtool.markdown.Block;

/**
 * markdown语法块
 * @author yangyingqiang
 * @time 2015年4月22日 上午11:44:22
 */
public interface BlockBuilder {

	/**
	 * 创建语法块
	 * @return
	 */
	public Block bulid();
	
	/**
	 * 检查内容是否属于当前语法块
	 * @return
	 */
	public boolean isRightType();
}
