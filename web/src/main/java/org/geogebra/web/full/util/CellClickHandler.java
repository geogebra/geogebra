package org.geogebra.web.full.util;

import elemental2.dom.Event;

public interface CellClickHandler {
	/**
	 * @param row table row
	 * @param col table column
	 * @param element cell element
	 * @return whether to stop propagation
	 */
	boolean onClick(int row, int col, Event element);
}
