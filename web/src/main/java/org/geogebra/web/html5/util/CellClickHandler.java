package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;

public interface CellClickHandler {
	/**
	 * @param row table row
	 * @param col table column
	 * @param element cell element
	 * @return whether to stop propagation
	 */
	boolean onClick(int row, int col, Element element);
}
