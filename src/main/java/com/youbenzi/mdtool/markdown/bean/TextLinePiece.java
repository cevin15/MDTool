package com.youbenzi.mdtool.markdown.bean;

public class TextLinePiece {
	private int beginIndex;
	private int endIndex;
	private String title;
	private String url;
	private PieceType pieceType;
	
	public TextLinePiece(int beginIndex, int endIndex, PieceType pieceType) {
		super();
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.pieceType = pieceType;
	}

	public TextLinePiece() {
		super();
	}

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

	public PieceType getPieceType() {
		return pieceType;
	}

	public void setPieceType(PieceType pieceType) {
		this.pieceType = pieceType;
	}

	public enum PieceType {
		LINK, IMAGE, COMMON
	}
}