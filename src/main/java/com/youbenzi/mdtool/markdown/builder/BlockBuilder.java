package com.youbenzi.mdtool.markdown.builder;

import com.youbenzi.mdtool.markdown.bean.Block;

/**
 * markdown语法块
 * @author yangyingqiang
 * 2015年4月22日 上午11:44:22
 */
public interface BlockBuilder {

	/**
	 * 创建语法块
	 * @return 语法块
	 */
	public Block bulid();
	
	/**
	 * 检查内容是否属于当前语法块
	 * @return true：是，false：否
	 */
	public boolean isRightType();
}
