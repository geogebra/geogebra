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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyPressHandler;

/**
 * Web dialog for angle
 */
public class InputDialogAngleFixedW extends AngleInputDialogW implements KeyPressHandler {
	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

	private GeoSegmentND[] segments;
	private GeoPointND[] points;

	private Kernel kernel;

	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param segments
	 *            selected segments
	 * @param points
	 *            selected points
	 * @param kernel
	 *            kernel
	 * @param ec
	 *            controller
	 */
	public InputDialogAngleFixedW(AppW app, DialogData data, InputHandler handler,
			GeoSegmentND[] segments, GeoPointND[] points, Kernel kernel,
			EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), data,
				defaultRotateAngle, handler, false);
		this.segments = segments;
		this.points = points;
		this.kernel = kernel;
		this.ec = ec;
	}

	@Override
	public void processInput() {
		final String inputText = getInputText();
		DialogManager.createAngleFixed(kernel, inputText,
				isClockWise(), this, segments, points,
				ok -> doProcessInput(ok, inputText), ec);
	}

	/**
	 * @param ok
	 *            input valid?
	 * @param inputText
	 *            input
	 */
	protected void doProcessInput(Boolean ok, String inputText) {
		if (ok) {
			// keep angle entered if it ends with 'degrees'
			if (inputText.endsWith(Unicode.DEGREE_STRING)) {
				defaultRotateAngle = inputText;
			} else {
				defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;
			}
		}
		setVisibleForTools(!ok);
	}

	/**
	 * @param visible
	 *            whether dialog should stay visible
	 */
	protected void setVisibleForTools(boolean visible) {
		if (!visible) {
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	/*
	 * auto-insert degree symbol when appropriate
	 * needs to be done in onKeyPress because only the KeyPress event has getCharCode method
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
		AutoCompleteTextFieldW tc = getTextComponent();
		String text = tc.getText();

		String input = StringUtil.addDegreeSignIfNumber(event.getCharCode(), text);

		int caretPos = tc.getCaretPosition();
		tc.setText(input);
		tc.setCaretPosition(caretPos);
	}
}