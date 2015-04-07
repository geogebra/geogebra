package org.geogebra.desktop.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.text.JTextComponent;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

public class InputDialogAngleFixed extends AngleInputDialog implements
		KeyListener {

	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	GeoSegmentND[] segments;
	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;

	private EuclidianController ec;

	public InputDialogAngleFixed(AppD app, String title, InputHandler handler,
			GeoSegmentND[] segments, GeoPointND[] points, GeoElement[] selGeos,
			Kernel kernel, EuclidianController ec) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false,
				handler, false);

		this.segments = segments;
		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

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
				setVisibleForTools(!processInput());
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisibleForTools(false);
		}
	}

	private boolean processInput() {

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		inputText = inputPanel.getText();

		// negative orientation ?
		if (rbClockWise.isSelected()) {
			inputText = "-(" + inputText + ")";
		}

		boolean success = inputHandler.processInput(inputText);

		cons.setSuppressLabelCreation(oldVal);

		if (success) {
			String angleText = getText();
			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0")) {
				defaultRotateAngle = angleText;
			} else {
				defaultRotateAngle = "45" + "\u00b0";
			}
			DialogManager.doAngleFixed(kernel, segments, points, selGeos,
					((NumberInputHandler) inputHandler).getNum(),
					rbClockWise.isSelected(), ec);

			return true;
		}

		return false;

	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	/*
	 * auto-insert degree symbol when appropriate
	 */
	public void keyReleased(KeyEvent e) {

		// return unless digit typed
		if (!Character.isDigit(e.getKeyChar()))
			return;

		JTextComponent tc = inputPanel.getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i)))
				return;
		}

		int caretPos = tc.getCaretPosition();

		tc.setText(tc.getText() + Unicode.degree);

		tc.setCaretPosition(caretPos);
	}
}
