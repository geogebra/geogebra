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
		double x = safeArea.getMinX();
		double y = inputBounds.getMaxY();
		double width = safeArea.getWidth();
		double height = popupSize.getHeight();

		Rectangle popupRectangle = new Rectangle(new XYPoint(x, y), new Size(width, height));

		if (safeArea.getMaxY() < popupRectangle.getMaxY()) {
			double delta = -(inputBounds.getHeight() + popupRectangle.getHeight());
			popupRectangle.moveVertically(delta);
		}
		if (safeArea.getMinY() > popupRectangle.getMinY()) {
			double spaceBelowInput = safeArea.getMaxY() - inputBounds.getMaxY();
			double spaceAboveInput = inputBounds.getMinY() - safeArea.getMinY();
			if (spaceBelowInput >= spaceAboveInput) {
				popupRectangle = new Rectangle(
						x, x + width, inputBounds.getMaxY(), safeArea.getMaxY());
			} else {
				popupRectangle = new Rectangle(
						x, x + width, safeArea.getMinY(), inputBounds.getMinY());
			}
		}

		return popupRectangle;
	}

	public Rectangle getSafeArea() {
		return safeArea;
	}
}
