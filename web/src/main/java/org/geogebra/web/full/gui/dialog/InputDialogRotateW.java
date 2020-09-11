package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Generic rotate dialog
 */
public abstract class InputDialogRotateW extends AngleInputDialogW implements KeyUpHandler {
	/** selcted polygons */
	GeoPolygon[] polys;
	/** selected geos */
	GeoElement[] selGeos;
	/** controller */
	protected EuclidianController ec;
	/** 45 degrees */
	final protected static String DEFAULT_ROTATE_ANGLE = Unicode.FORTY_FIVE_DEGREES_STRING;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotateW(AppW app, DialogData data,
			InputHandler handler, GeoPolygon[] polys, 
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), data,
				DEFAULT_ROTATE_ANGLE, handler, false);

		this.polys = polys;
		this.selGeos = selGeos;
		this.ec = ec;

		getTextComponent().getTextField().getValueBox().addKeyUpHandler(this);
	}

	/*@Override
	protected void actionPerformed(DomEvent<?> e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				//
				processInput(new AsyncOperation<String>() {

					@Override
					public void callback(String obj) {
						// FIXME setVisibleForTools(!processInput());
						if (obj == null) {
							// wrappedPopup.show();
							getTextComponent().hideTablePopup();
						} else {
							setVisible(false);

						}
					}
				});

			} /*else if (source == btCancel) {
				setVisible(false);

			}*/
	/*} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
	}*/

	/**
	 * @param op
	 *            callback
	 */
	protected abstract void processInput(AsyncOperation<String> op);

	/*
	 * auto-insert degree symbol when appropriate
	 */
	@Override
	public void onKeyUp(KeyUpEvent e) {
		// return unless digit typed (instead of !Character.isDigit)
		if (e.getNativeKeyCode() < 48
				|| (e.getNativeKeyCode() > 57 && e.getNativeKeyCode() < 96)
				|| e.getNativeKeyCode() > 105) {
			return;
		}

		AutoCompleteTextFieldW tc = getTextComponent();
		String text = getInputText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i))) {
				return;
			}
		}

		int caretPos = tc.getCaretPosition();
		tc.setText(tc.getText() + Unicode.DEGREE_STRING);
		tc.setCaretPosition(caretPos);
	}
}