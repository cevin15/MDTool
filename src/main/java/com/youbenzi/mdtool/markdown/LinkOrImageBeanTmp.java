package com.youbenzi.mdtool.markdown;

public class LinkOrImageBeanTmp {
	private int beginIndex;
	private int endIndex;
	private String title;
	private String url;
	private boolean isLink;

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isLink() {
		return isLink;
	}

	public void setLink(boolean isLink) {
		this.isLink = isLink;
	}
}