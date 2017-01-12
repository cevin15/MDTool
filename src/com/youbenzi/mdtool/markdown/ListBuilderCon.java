package com.youbenzi.mdtool.markdown;

public abstract class ListBuilderCon{
	public abstract boolean isRightType(String lineStr);
	public abstract Block newBuilder(String str);
	public StringBuilder how2AppendIfBlank(StringBuilder sb){
		return sb;
	}
	
	public StringBuilder how2AppendIfNotBlank(StringBuilder sb, String value){
		sb.append(value+"\n");
		return sb;
	}
}