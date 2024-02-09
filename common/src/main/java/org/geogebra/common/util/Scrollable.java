package org.geogebra.common.util;

public interface Scrollable {

	void setVerticalScrollPosition(int position);

	int getVerticalScrollPosition();

	void setHorizontalScrollPosition(int position);

	int getHorizontalScrollPosition();

	int getScrollBarWidth();
}