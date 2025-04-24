package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

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
		GPoint center = getCellCenter(controller, row, column);
		for (int click = 0; click < nrClicks; click++) {
			controller.handlePointerDown(center.x, center.y, Modifiers.NONE);
			controller.handlePointerUp(center.x, center.y, Modifiers.NONE);
		}
	}

	private static GPoint getCellCenter(SpreadsheetController controller, int row, int column) {
		TableLayout layout = controller.getLayout();
		Rectangle cellBounds = layout.getBounds(row, column)
				.translatedBy(layout.getRowHeaderWidth(), layout.getColumnHeaderHeight());
		return new GPoint((int) (cellBounds.getMinX() + cellBounds.getWidth() / 2),
				(int) (cellBounds.getMinY() + cellBounds.getWidth() / 2));
	}
}
