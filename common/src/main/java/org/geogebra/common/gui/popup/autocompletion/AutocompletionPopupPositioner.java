package org.geogebra.common.gui.popup.autocompletion;

import javax.annotation.Nonnull;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

/**
 * The AutocompletionPopupPositioner calculates
 * the position of the autocomplete (or suggestion) popup.
 * A new instance of this class has to be created on orientation change.
 */
public class AutocompletionPopupPositioner {

	private static final int MAX_WIDTH = 520;
	private static final int MAX_HEIGHT = 228;
	private static final int MARGIN = 8;

	public Rectangle calculatePopupFrame(Rectangle inputBounds, Size popupSize, Rectangle frame) {
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
		if (height <= spaceBelow || (height > spaceAbove && spaceBelow > spaceAbove)) {
			// Popup below input bar
			y = inputBounds.getMaxY();
		} else {
			// Popup above input bar
			y = inputBounds.getMinY() - height;
		}

		return new Rectangle(x, x + width, y, y + height);
	}
}
