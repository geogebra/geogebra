package org.geogebra.web.web.gui.pagecontrolpanel;

public interface CardListener {
	void loadPage(PagePreviewCard card);
	void reorder(int srcIdx, int destIdx);
	void dropTo(int x, int y);
	void hover(int pageIndex);
	void makeSpace(int pageIndex, boolean before);
}