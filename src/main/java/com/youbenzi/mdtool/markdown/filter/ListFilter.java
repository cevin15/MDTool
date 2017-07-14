package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.List;

import com.youbenzi.mdtool.markdown.Block;
import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.MultiListBuilder;

/**
 * 筛选出内容中的列表
 * @author yangyingqiang
 * @time 2017年7月14日 下午4:43:35
 */
public class ListFilter extends SyntaxFilter {

	public ListFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();
		for (int idx = 0, si = lines.size(); idx < si; idx++) {
			String str = lines.get(idx);
			if (isListLine(str)) {	//列表开始
				StringBuilder interText = new StringBuilder(trim(str)).append("\n");
				for (int idx1 = (idx + 1); idx1 < si; idx1++) {
					str = lines.get(idx1);
					if(str.trim().equals("")){
						how2AppendIfBlank(interText);
					} else {
						if(!isListLine(str)) {	//列表结束，跳出循环
							idx = idx1 - 1;		//外部循环开始读数据的地方
							break;
						}else{
							interText.append(trim(str)).append("\n");
						}
					}
					if(idx1 == (si - 1)) {	//列表已无可读数据，通知外部循环不需要再继续读取数据
						idx = idx1;
					}
				}
				
				if (!outerText.toString().equals("")) {
					textOrBlocks.add(new TextOrBlock(outerText.toString()));
					outerText = new StringBuilder();
				}
				textOrBlocks.add(new TextOrBlock(buildBlock(interText.toString())));
			} else {
				outerText.append(str + "\n");
			}
		}
		if (!outerText.toString().equals("")) {
			textOrBlocks.add(new TextOrBlock(outerText.toString()));
		}
		return textOrBlocks;
	}

	/**
	 * 是否是符合规则的列表
	 * @param target
	 * @return
	 */
	protected boolean isListLine(String target) {
		return MultiListBuilder.isList(target);
	}
	
	/**
	 * 对内容进行无用字符截取
	 * @param target
	 * @return
	 */
	protected String trim(String target) {
		//do nothing
		return target;
	}
	
	/**
	 * 创建对应的block
	 * @param source
	 * @return
	 */
	protected Block buildBlock(String source) {
		return new MultiListBuilder(source).bulid();
	}
	
	/**
	 * 如果数据为空怎么处理
	 * @param target
	 */
	protected void how2AppendIfBlank(StringBuilder target) {
		//do nothing
	}
}
