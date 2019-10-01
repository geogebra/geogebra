package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.debug.Log;

public abstract class ViewTextField {

	/**
	 * @return textfield (may be null)
	 */
	public abstract AutoCompleteTextField getTextField();

	/**
	 * @param drawInputBox
	 *            input box to focus
	 */
	public void focusTo(DrawInputBox drawInputBox) {
		if (getTextField() == null) {
			Log.debug("[TF] textField is null");
			return;
		}
		GeoInputBox geoInputBox = (GeoInputBox) drawInputBox.getGeoElement();
		getTextField().setAuralText(geoInputBox.getAuralText());
		drawInputBox.attachTextField();
	}


	private void applyChanges() {
		if (getTextField() == null) {
			return;
		}
		getTextField().applyToInputBox();
	}

	/**
	 * Revalidate the Swing component
	 */
	public void revalidateBox() {
		// only in desktop
	}

	/**
	 * @param isVisible
	 *            visibility of the wrapping box
	 */
	public abstract void setBoxVisible(boolean isVisible);

	/**
	 * @param labelRectangle
	 *            wrapping box bounds
	 */
	public abstract void setBoxBounds(GRectangle labelRectangle);

	/**
	 * Paint all components to graphics
	 *
	 * @param g2
	 *            graphics
	 */
	public void repaintBox(GGraphics2D g2) {
		// only in desktop
	}

	/**
	 * Hide both text field and box
	 */
	public void hideDeferred() {
		// only in desktop
	}

	/**
	 * @param length
	 *            number of characters
	 * @param drawInputBox
	 *            linked drawable
	 * @return textfield
	 */
	protected abstract AutoCompleteTextField getTextField(int length, DrawInputBox drawInputBox);
}