/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;

public class ViewTextFieldW extends ViewTextField {

	private SimplePanel box;
	private AutoCompleteTextFieldW textField;
	private final EuclidianViewWInterface euclidianView;
	private int hideRequest;

	public ViewTextFieldW(EuclidianViewWInterface euclidianView) {
		this.euclidianView = euclidianView;
	}

	private AutoCompleteTextFieldW newAutoCompleteTextField(int length,
			Drawable drawTextField) {
		AutoCompleteTextFieldW textFieldW = new AutoCompleteTextFieldW(length,
				this.euclidianView.getApplication(), drawTextField);
		return textFieldW;
	}

	private void ensureBoxExists() {
		if (box == null) {
			box = new SimplePanel();
			box.addStyleName("gbox");
			box.setWidget(textField);
			EventUtil.stopPointerEvents(box.getElement(), btn -> btn <= 0);
		}
	}

	@Override
	public void setBoxVisible(boolean isVisible) {
		ensureBoxExists();
		((EuclidianViewW) euclidianView).doRepaint();
		DomGlobal.cancelAnimationFrame(hideRequest);
		if (isVisible) {
			textField.enableGGBKeyboard();
			box.setVisible(true);
		} else {
			// deferred so that the canvas version can be drawn
			hideRequest = DomGlobal.requestAnimationFrame(e -> box.setVisible(false));
		}
	}

	@Override
	public void setBoxBounds(GRectangle bounds) {
		ensureBoxExists();
		if (box.getParent() != null) {
			((AbsolutePanel) box.getParent()).setWidgetPosition(box,
					(int) bounds.getMinX(), (int) bounds.getMinY());
		}
	}

	@Override
	public AutoCompleteTextField getTextField(int length,
			DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = newAutoCompleteTextField(length, drawInputBox);
			textField.setAutoComplete(false);
			ensureBoxExists();
			box.setWidget(textField);
			euclidianView.add(box);
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
		box = null;
	}
}
