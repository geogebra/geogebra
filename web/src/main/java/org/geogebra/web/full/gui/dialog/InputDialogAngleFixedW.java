package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.himamis.retex.editor.share.util.Unicode;

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
	 *            selcted points
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
				rbClockWise.getValue(), this, segments, points,
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
		getTextComponent().hideTablePopup();
	}

	/*
 	* auto-insert degree symbol when appropriate
 	*/
	//moved the operations from onKeyUp to onKeyPress,
	//because only the KeyPress event has getCharCode method
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