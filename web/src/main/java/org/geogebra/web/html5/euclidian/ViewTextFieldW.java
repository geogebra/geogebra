package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.javax.swing.Positioner;

import com.google.gwt.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;

public class ViewTextFieldW extends ViewTextField {

	private Positioner positioner;
	private SimplePanel box;
	private AutoCompleteTextFieldW textField;
	private final EuclidianViewWInterface euclidianView;
	private int hideRequest;

	public ViewTextFieldW(EuclidianViewWInterface euclidianView) {
		this.euclidianView = euclidianView;
	}

	private AutoCompleteTextFieldW newAutoCompleteTextField(int length,
			Drawable drawTextField) {
		return new AutoCompleteTextFieldW(length,
				this.euclidianView.getApplication(), drawTextField, true);
	}

	private void ensureBoxExists() {
		if (box == null) {
			box = new SimplePanel();
			box.addStyleName("gbox");
			positioner = new Positioner(euclidianView.getEuclidianController(), box);
			box.setWidget(textField);
		}
	}

	@Override
	public void setBoxVisible(boolean isVisible) {
		ensureBoxExists();
		((EuclidianViewW) euclidianView).doRepaint();
		DomGlobal.cancelAnimationFrame(hideRequest);
		if (isVisible) {
			box.setVisible(true);
		} else {
			// deferred so that the canvas version can be drawn
			hideRequest = DomGlobal.requestAnimationFrame(e -> box.setVisible(false));
		}
	}

	@Override
	public void setBoxBounds(GRectangle bounds) {
		ensureBoxExists();
		positioner.setPosition(bounds.getMinX(), bounds.getMinY());
	}

	@Override
	public AutoCompleteTextField getTextField(int length,
			DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = newAutoCompleteTextField(length, drawInputBox);
			textField.setAutoComplete(false);
			ensureBoxExists();
			box.setWidget(textField);
			euclidianView.add(box, positioner.getPosition());
		} else {
			textField.setDrawTextField(drawInputBox);
		}

		return textField;
	}

	@Override
	public AutoCompleteTextField getTextField() {
		return textField;
	}

	@Override
	public void remove() {
		textField = null;
		positioner = null;
		box = null;
	}
}
