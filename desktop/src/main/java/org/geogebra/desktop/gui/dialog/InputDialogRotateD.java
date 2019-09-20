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

import com.himamis.retex.editor.share.util.Unicode;

public abstract class InputDialogRotateD extends AngleInputDialogD
		implements KeyListener {

	protected GeoPolygon[] polys;
	protected GeoElement[] selGeos;

	protected EuclidianController ec; // we need to know which controller called
										// for rotate

	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

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
				processInput(new AsyncOperation<String>() {

					@Override
					public void callback(String obj) {
						setVisibleForTools(obj == null);
						if (obj != null) {
							defaultRotateAngle = obj;
						}
					}
				});
			} else if (source == btApply) {
				processInput(new AsyncOperation<String>() {

					@Override
					public void callback(String obj) {
						// TODO Auto-generated method stub

					}
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
