package org.geogebra.desktop.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.text.JTextComponent;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.editor.share.util.Unicode;

public class InputDialogAngleFixedD extends AngleInputDialogD
		implements KeyListener {

	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

	GeoSegmentND[] segments;
	GeoPointND[] points;

	private Kernel kernel;

	private EuclidianController ec;

	public InputDialogAngleFixedD(AppD app, String title, InputHandler handler,
			GeoSegmentND[] segments, GeoPointND[] points, Kernel kernel,
			EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), title,
				defaultRotateAngle, false, handler, false);

		this.segments = segments;
		this.points = points;
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
				processInput();
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

	private void processInput() {
		final String inputText = inputPanel.getText();
		DialogManager.createAngleFixed(kernel, inputText,
				rbClockWise.isSelected(), app.getErrorHandler(), segments,
				points, new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
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
				}, ec);

	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.resetCurrentSelectionListener();
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// only handle key release
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// only handle key release
	}

	/*
	 * auto-insert degree symbol when appropriate
	 */
	@Override
	public void keyReleased(KeyEvent e) {

		JTextComponent tc = inputPanel.getTextComponent();
		String text = tc.getText();

		String input = StringUtil.addDegreeSignIfNumber(e.getKeyChar(), text);
		int caretPos = tc.getCaretPosition();

		tc.setText(input);

		tc.setCaretPosition(caretPos);
	}
}
