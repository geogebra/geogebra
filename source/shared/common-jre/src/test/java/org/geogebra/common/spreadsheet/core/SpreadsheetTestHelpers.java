/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.editor.share.util.JavaKeyCodes;

final class SpreadsheetTestHelpers {

	static void simulateLeftArrowPress(SpreadsheetController controller) {
		controller.handleKeyPressed(JavaKeyCodes.VK_LEFT, "", Modifiers.NONE);
	}

	static void simulateUpArrowPress(SpreadsheetController controller) {
		controller.handleKeyPressed(JavaKeyCodes.VK_UP, "", Modifiers.NONE);
	}

	static void simulateRightArrowPress(SpreadsheetController controller) {
		controller.handleKeyPressed(JavaKeyCodes.VK_RIGHT, "", Modifiers.NONE);
	}

	static void simulateDownArrowPress(SpreadsheetController controller) {
		controller.handleKeyPressed(JavaKeyCodes.VK_DOWN, "", Modifiers.NONE);
	}

	static void simulateCellMouseClick(SpreadsheetController controller, int row, int column,
			int nrClicks) {
		Point center = getCellCenter(controller, row, column);
		for (int click = 0; click < nrClicks; click++) {
			controller.handlePointerDown(center.x, center.y, Modifiers.NONE);
			controller.handlePointerUp(center.x, center.y, Modifiers.NONE);
		}
	}

	static void simulateColumnResize(SpreadsheetController controller, int column, double delta) {
		TableLayout layout = controller.getLayout();
		Rectangle cellBounds = layout.getBounds(0, column);
		double cellResizeDragX = cellBounds.getMaxX() + layout.getRowHeaderWidth();
		double cellResizeDragY = layout.getColumnHeaderHeight() / 2;
		controller.handlePointerDown(cellResizeDragX, cellResizeDragY, Modifiers.NONE);
		controller.handlePointerMove(cellResizeDragX + delta, cellResizeDragY, Modifiers.NONE);
		controller.handlePointerUp(cellResizeDragX + delta, cellResizeDragY, Modifiers.NONE);
	}

	static Point getCellCenter(SpreadsheetController controller, int row, int column) {
		TableLayout layout = controller.getLayout();
		if (row == -1 && column >= 0) {
			// entire column, return center of column header
			Rectangle cellBounds = layout.getBounds(0, column);
			return new Point(layout.getRowHeaderWidth() + cellBounds.getMidX(),
					layout.getColumnHeaderHeight() / 2);
		}
		if (column == -1 && row >= 0) {
			// entire row, return center of row header
			Rectangle cellBounds = layout.getBounds(row, 0);
			return new Point(layout.getRowHeaderWidth() / 2,
					layout.getColumnHeaderHeight() + cellBounds.getMidY());
		}
		Rectangle cellBounds = layout.getBounds(row, column)
				.translatedBy(layout.getRowHeaderWidth(), layout.getColumnHeaderHeight());
		return new Point(cellBounds.getMidX(), cellBounds.getMidY());
	}
}
