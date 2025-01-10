package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.debug.Log;

/**
 * Adapter between input box widget and drawable
 */
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
		if (geoInputBox != getTextField().getInputBox()) {
			applyChanges();
		}

		getTextField().setAuralText(geoInputBox.getAuralText());
		drawInputBox.attachTextField();
	}

	/**
	 * Apply the edited text to the corresponding GeoInputBox
	 */
	protected void applyChanges() {
		AutoCompleteTextField textField = getTextField();
		if (textField == null) {
			return;
		}
		GeoInputBox geoInputBox = textField.getInputBox();

		if (geoInputBox == null) {
			return;
		}

		if (textField.getText().equals(geoInputBox.getText())) {
			return;
		}

		geoInputBox.textObjectUpdated(textField);
		geoInputBox.textSubmitted();
		geoInputBox.updateRepaint();
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

	/**
	 * Remove referenced objects.
	 */
	public abstract void remove();

	/**
	 * @param length
	 *            number of characters
	 */
	public void setColumns(int length) {
		// only in desktop
	}

	/**
	 * Resets textfield
	 */
	public void reset() {
		if (getTextField() != null) {
			getTextField().setUsedForInputBox(null);
		}
	}
}
