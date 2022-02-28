package org.geogebra.common.gui.popup.autocompletion;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

/**
 * The AutocompletionPopupPositioner calculates
 * the position of the autocomplete (or suggestion) popup.
 */
public class AutocompletionPopupPositioner {

	private static final int MAX_WIDTH = 520;
	private static final int MAX_HEIGHT = 228;
	private static final int MARGIN = 8;

	// The size of the syntax popup header, the margin and one row
	private static final int MIN_SPACE = 48 + 8 + 40;

	/**
	 * Calculates the frame of the popup.
	 * @param inputBounds the frame of the input view
	 * @param popupSize the preferred size of the pupup to be displayed
	 * @param frame the frame in which we display the popup
	 * @param verticalPosition the vertical position of the popup
	 * @return the frame of the popup
	 */
	public Rectangle calculatePopupFrame(Rectangle inputBounds, Size popupSize, Rectangle frame,
			VerticalPosition verticalPosition) {
		// Position
		double x, y;

		// Restrict popup size to max values
		double width = MAX_WIDTH;
		double height = Math.min(popupSize.getHeight(), MAX_HEIGHT);

		// Restrict popup size to frame width with margins
		if (width > frame.getWidth() - 2 * MARGIN) {
			width = frame.getWidth() - 2 * MARGIN;
		}

		// Horizontal positioning
		if (frame.getWidth() - 2 * MARGIN > width) {
			// Popup aligned to the left of the input
			x = inputBounds.getMinX();

			// Change width to still fit into screen
			if (width > frame.getWidth() - x - MARGIN) {
				width = frame.getWidth() - x - MARGIN;
			}
		} else {
			// Popup aligned to the left of the frame with margin
			x = MARGIN;
		}

		// Vertical positioning
		double spaceBelow = frame.getMaxY() - inputBounds.getMaxY();
		double spaceAbove = inputBounds.getMinY() - frame.getMinY();
		double requiredHeight = Math.max(height, MIN_SPACE);
		if (verticalPosition == VerticalPosition.BELOW
				|| (verticalPosition != VerticalPosition.ABOVE
				&& (requiredHeight <= spaceBelow || spaceBelow > spaceAbove))) {
			// Popup below input bar
			y = inputBounds.getMaxY();
			// Restrict height to remaining space
			if (height > spaceBelow) {
				height = spaceBelow;
			}
		} else {
			// Restrict height to remaining space
			if (height > spaceAbove) {
				height = spaceAbove;
			}
			// Popup above input bar
			y = inputBounds.getMinY() - height;
		}

		return new Rectangle(x, x + width, y, y + height);
	}
}
