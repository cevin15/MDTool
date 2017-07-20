package com.youbenzi.mdtool.markdown.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.youbenzi.mdtool.markdown.TextOrBlock;
import com.youbenzi.mdtool.markdown.builder.TableBuilder;

/**
 * 筛选出内容中的表格
 * @author yangyingqiang
 * 2017年7月14日 上午11:44:13
 */
public class TablePartFilter extends SyntaxFilter{

	public TablePartFilter(SyntaxFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public List<TextOrBlock> invoke(List<String> lines) {
		List<TextOrBlock> textOrBlocks = new ArrayList<>();
		StringBuilder outerText = new StringBuilder();
		for (int i = 0, l = lines.size(); i < l; i++) {
			String str = lines.get(i);
			boolean hasTable = false;
			if (str.indexOf("|") > -1) { // 检查是否有table的分隔符
				hasTable = true;
				boolean isStart = false;
				boolean isEnd = false;
				if (str.startsWith("\\|")) { // 去头
					str = str.substring(1);
					isStart = true;
				}
				if (str.endsWith("\\|")) { // 去尾
					str = str.substring(0, str.length() - 1);
					isEnd = true;
				}
				String[] tmps = str.split("\\|");
				if (tmps.length <= 1 && !(isStart && isEnd)) {
					hasTable = false;
				}
			}
			if (hasTable) {
				if ((i + 1) < l) { // 检查到符合规范的table头之后，检测下一行是否为 ---|---的类似字符串
					String nextLine = lines.get(i + 1);
					String[] nextParts = nextLine.split("\\|");
					for (String part : nextParts) {

						part = part.trim().replaceAll("-", "");
						if (part.length() > 0) {
							hasTable = false;
						}
						if (!hasTable) {
							break;
						}
					}
				} else {
					hasTable = false;
				}
			}
			if (hasTable) { // 检查到真的有table存在
				if (!outerText.toString().equals("")) { // 把已存入stringbuffer的内容先归档
					textOrBlocks.add(new TextOrBlock(outerText.toString()));
					outerText = new StringBuilder();
				}

				List<List<String>> tableDataList = new ArrayList<List<String>>();
				int tableLineNum = i + 1; // ---|---的行数，此行不能放入table的data
				for (int j = i; j < l; j++) {
					if (j == tableLineNum) {
						continue;
					}
					String tableLine = lines.get(j);
					String[] cellDatas = tableLine.split("\\|");
					if (cellDatas.length >= 2) { // 此行是table的数据
						tableDataList.add(Arrays.asList(cellDatas));
						if (j == (l - 1)) { // 到内容底部，table数据结束，归档
							tableDataList = trimTableData(tableDataList);
							textOrBlocks.add(new TextOrBlock(new TableBuilder(tableDataList).bulid()));

							i = j; // 设置游标，跳出循环
							break;
						}
					} else { // table数据结束，归档
						tableDataList = trimTableData(tableDataList);
						textOrBlocks.add(new TextOrBlock(new TableBuilder(tableDataList).bulid()));

						i = (j - 1); // 设置游标，跳出循环
						break;
					}
				}
			} else {
				outerText.append(str + "\n");
			}
		}
		if (!outerText.toString().equals("")) {
			textOrBlocks.add(new TextOrBlock(outerText.toString()));
		}
		return textOrBlocks;
	}

	private List<List<String>> trimTableData(List<List<String>> tableDataList) {
		boolean isFirstEmpty = true;
		boolean isLastEmpty = true;
		for (int k = 0, m = tableDataList.size(); k < m; k++) {
			List<String> tmps = tableDataList.get(k);
			if (!tmps.get(0).trim().equals("") && isFirstEmpty) {
				isFirstEmpty = false;
			}
			if (!tmps.get(tmps.size() - 1).trim().equals("") && isLastEmpty) {
				isLastEmpty = false;
			}
		}
		if (isLastEmpty) {
			for (int k = 0, m = tableDataList.size(); k < m; k++) {
				List<String> tmps = tableDataList.get(k);
				List<String> newTmps = new ArrayList<String>();
				for (int n = 0, o = tmps.size(); n < o; n++) {
					if (n < (o - 1)) {
						newTmps.add(tmps.get(n));
					}
				}
				tableDataList.set(k, newTmps);
			}
		}
		if (isFirstEmpty) {
			for (int k = 0, m = tableDataList.size(); k < m; k++) {
				List<String> tmps = tableDataList.get(k);
				List<String> newTmps = new ArrayList<String>();
				for (int n = 0, o = tmps.size(); n < o; n++) {
					if (n > 0) {
						newTmps.add(tmps.get(n));
					}
				}
				tableDataList.set(k, newTmps);
			}
		}
		return tableDataList;
	}
}
