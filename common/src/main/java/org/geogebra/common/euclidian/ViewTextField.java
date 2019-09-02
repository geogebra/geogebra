package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public abstract class ViewTextField {
	/**
	 *
	 */
	private final EuclidianView euclidianView;
	private AutoCompleteTextField textField;
	private GBox box;

	/**
	 * @param euclidianView
	 *            view
	 */
	public ViewTextField(EuclidianView euclidianView) {
		this.euclidianView = euclidianView;
		textField = null;
		box = null;
	}

	/**
	 * @param length
	 *            number of characters
	 * @param drawInputBox
	 *            linked drawable
	 * @return textfield
	 */
	public AutoCompleteTextField getTextField(int length,
			DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = newAutoCompleteTextField(length, this.euclidianView.app,
					drawInputBox);
			textField.setAutoComplete(false);
			textField.enableColoring(false);
			textField.setFocusTraversalKeysEnabled(false);
			createBox();
			box.add(textField);
			this.euclidianView.add(box);
		} else {
			textField.setDrawTextField(drawInputBox);
		}

		return textField;
	}

	/**
	 * @return textfield (may be null)
	 */
	public AutoCompleteTextField getTextField() {
		return textField;
	}

	/**
	 * @param inputBox
	 *            input box to focus
	 */
	public void focusTo(GeoInputBox inputBox) {
		if (inputBox.isSymbolicMode()) {
			focusToSymbolicEditor(inputBox);
			return;
		}

		if (textField == null) {
			Log.debug("[TF] textField is null");
			return;
		}
		textField.setAuralText(inputBox.getAuralText());
		DrawableND d = this.euclidianView
				.getDrawableFor(inputBox);
		if (d == null) {
			Log.debug("[TF] d is null!!!");
			return;
		}
		((DrawInputBox) d).attachTextField();
	}

	private void focusToSymbolicEditor(GeoInputBox inputBox) {
		DrawableND d = this.euclidianView
				.getDrawableFor(inputBox);
		if (d != null) {
			((DrawInputBox) d).attachMathField();
		}
	}

	/**
	 * @return box wrapping the input field
	 */
	public GBox getBox() {
		createBox();
		return box;
	}

	private void createBox() {
		if (box == null) {
			box = createHorizontalBox(
					this.euclidianView.getEuclidianController());
			box.add(textField);
		}
	}

	public abstract AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField);

	public abstract GBox createHorizontalBox(EuclidianController style);

	/**
	 * Remove referenced objects.
	 */
	public void remove() {
		textField = null;
		box = null;
	}

}