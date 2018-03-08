package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.himamis.retex.editor.share.util.Unicode;

public abstract class InputDialogRotateW extends AngleInputDialogW {

	GeoPolygon[] polys;
	GeoElement[] selGeos;
	
	protected EuclidianController ec;

	/** 45 degrees */
	final protected static String DEFAULT_ROTATE_ANGLE = Unicode.FORTY_FIVE_DEGREES_STRING;

	public InputDialogRotateW(AppW app, String title,
			InputHandler handler, GeoPolygon[] polys, 
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), title,
				DEFAULT_ROTATE_ANGLE, false,
				handler, false);

		this.polys = polys;
		this.selGeos = selGeos;
		this.ec = ec;

		this.inputPanel.getTextComponent().getTextField().getValueBox().addKeyUpHandler(this);
	}

	/**
	 * Handles button clicks for dialog.
	 */	
	@Override
	public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	@Override
	protected void actionPerformed(DomEvent e) {
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
							inputPanel.getTextComponent().hideTablePopup();
						} else {
							wrappedPopup.hide();
							inputPanel.getTextComponent().hideTablePopup();
							app.getActiveEuclidianView().requestFocusInWindow();
						}
					}
				});

			//} else if (source == btApply) {
			//	processInput();
			} else if (source == btCancel) {
				//FIXME setVisibleForTools(false);
				wrappedPopup.hide();
				inputPanel.getTextComponent().hideTablePopup();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			//FIXME setVisibleForTools(false);
			wrappedPopup.hide();
			inputPanel.getTextComponent().hideTablePopup();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	protected abstract void processInput(AsyncOperation<String> op);

	/*
	 * auto-insert degree symbol when appropriate
	 */
	@Override
	public void onKeyUp(KeyUpEvent e) {

		// return unless digit typed (instead of !Character.isDigit)
		if (e.getNativeKeyCode() < 48 ||
			(e.getNativeKeyCode() >  57 && e.getNativeKeyCode() < 96) ||
			e.getNativeKeyCode() > 105) {
			return;
		}

		AutoCompleteTextFieldW tc = inputPanel.getTextComponent();
		String text = tc.getText();

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
