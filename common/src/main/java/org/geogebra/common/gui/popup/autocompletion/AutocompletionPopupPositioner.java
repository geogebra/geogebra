package org.geogebra.common.gui.popup.autocompletion;

import javax.annotation.Nonnull;

import org.geogebra.common.util.shape.XYPoint;
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

	@Nonnull
	public Rectangle calculatePositionAndSizeFor(Rectangle inputBounds, Size popupSize) {
		double fittingWidth = popupSize.getWidth();
		double fittingHeight = popupSize.getHeight();
		Size dummyFittingSize = new Size(fittingWidth, fittingHeight);

		double x = 0;
		double y = inputBounds.getMinY() - fittingHeight;
		XYPoint dummyAbovePosition = new XYPoint(x, y);

		return new Rectangle(dummyAbovePosition, dummyFittingSize);
	}
}
