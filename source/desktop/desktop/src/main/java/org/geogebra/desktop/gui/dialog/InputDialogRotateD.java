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

package org.geogebra.desktop.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.text.JTextComponent;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.editor.share.util.Unicode;

public abstract class InputDialogRotateD extends AngleInputDialogD
		implements KeyListener {

	protected GeoPolygon[] polys;
	protected GeoElement[] selGeos;

	protected EuclidianController ec; // we need to know which controller called
										// for rotate

	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

	/**
	 * @param app application
	 * @param title title
	 * @param handler input handler
	 * @param polys polygons
	 * @param selGeos selected geos
	 * @param ec controller
	 */
	public InputDialogRotateD(AppD app, String title, InputHandler handler,
			GeoPolygon[] polys, GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), title,
				defaultRotateAngle, false, handler, false);

		this.polys = polys;
		this.selGeos = selGeos;

		this.ec = ec;

		this.inputPanel.getTextComponent().addKeyListener(this);

	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				processInput(obj -> {
					setVisibleForTools(obj == null);
					if (obj != null) {
						defaultRotateAngle = obj;
					}
				});
			} else if (source == btApply) {
				processInput(obj -> {
					// TODO Auto-generated method stub

				});
			} else if (source == btCancel) {
				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisibleForTools(false);
		}
	}

	protected abstract void processInput(AsyncOperation<String> op);

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.resetCurrentSelectionListener();
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// only handle release
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// only handle release
	}

	/*
	 * auto-insert degree symbol when appropriate
	 */
	@Override
	public void keyReleased(KeyEvent e) {

		// return unless digit typed
		if (!Character.isDigit(e.getKeyChar())) {
			return;
		}

		JTextComponent tc = inputPanel.getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i))) {
				return;
			}
		}

		int caretPos = tc.getCaretPosition();

		tc.setText(tc.getText() + Unicode.DEGREE_STRING);

		tc.setCaretPosition(caretPos);
	}
}
