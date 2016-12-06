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

	public ViewTextField(EuclidianView euclidianView) {
		this.euclidianView = euclidianView;
		textField = null;
		box = null;
	}

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

	public AutoCompleteTextField getTextField() {
		return textField;
	}

	public void focusTo(GeoInputBox inputBox) {

		DrawInputBox d = (DrawInputBox) this.euclidianView
				.getDrawableFor(inputBox);
		if (d == null) {
			Log.debug("[TF] d is null!!!");
			return;
		}
		Log.debug("[TF] focusTo " + inputBox.getLabelSimple() + " text: "
				+ inputBox.getText());
		d.attachTextField();
	}

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

	public void remove() {
		textField = null;
		box = null;
	}

}