package org.geogebra.common.gui.popup.autocompletion;

import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

/**
 * The AutocompletionPopupPositioner calculates
 * the position of the autocomplete (or suggestion) popup.
 * A new instance of this class has to be created on orientation change.
 */
public class AutocompletionPopupPositioner {

	private final Rectangle safeArea;
	private final Size popupMinSize;

	public AutocompletionPopupPositioner(Rectangle safeArea, Size popupMinSize) {
		this.safeArea = safeArea;
		this.popupMinSize = popupMinSize;
	}

	public Rectangle calculatePositionAndSizeFor(Rectangle inputBounds, Size popupSize) {
		double x = inputBounds.getMinX();
		double y = inputBounds.getMaxY();
		Point dummyPosition = new Point(x, y);

		double fittingWidth = popupSize.getWidth();
		double fittingHeight = popupSize.getHeight();
		Size dummyFittingSize = new Size(fittingWidth, fittingHeight);

		return new Rectangle(dummyPosition, dummyFittingSize);
	}
}
