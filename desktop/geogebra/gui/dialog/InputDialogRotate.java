package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.DialogManager;
import geogebra.common.util.Unicode;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.text.JTextComponent;

public class InputDialogRotate extends AngleInputDialog implements KeyListener {

	GeoPolygon[] polys;
	GeoPointND[] points;
	GeoElement[] selGeos;

	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	public InputDialogRotate(AppD app, String title, InputHandler handler,
			GeoPolygon[] polys, GeoPointND[] points, GeoElement[] selGeos) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false,
				handler, false);

		this.polys = polys;
		this.points = points;
		this.selGeos = selGeos;
		
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

		defaultRotateAngle = DialogManager.rotateObject(app,
				inputPanel.getText(), rbClockWise.isSelected(), polys, points,
				selGeos);

		return true;
		/*
		 * 
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * inputText = inputPanel.getText();
		 * 
		 * // negative orientation ? if (rbClockWise.isSelected()) { inputText =
		 * "-(" + inputText + ")"; }
		 * 
		 * boolean success = inputHandler.processInput(inputText);
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (success) { // GeoElement circle = kernel.Circle(null, geoPoint1,
		 * // ((NumberInputHandler)inputHandler).getNum()); NumberValue num =
		 * ((NumberInputHandler) inputHandler).getNum(); //
		 * geogebra.gui.AngleInputDialog dialog = //
		 * (geogebra.gui.AngleInputDialog) ob[1]; String angleText = getText();
		 * 
		 * // keep angle entered if it ends with 'degrees' if
		 * (angleText.endsWith("\u00b0")) defaultRotateAngle = angleText; else
		 * defaultRotateAngle = "45" + "\u00b0";
		 * 
		 * if (polys.length == 1) {
		 * 
		 * GeoElement[] geos = kernel.Rotate(null, polys[0], num, points[0]); if
		 * (geos != null) { app.storeUndoInfo();
		 * kernel.getApplication().getActiveEuclidianView()
		 * .getEuclidianController() .memorizeJustCreatedGeos(geos); } return
		 * true; } ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for
		 * (int i = 0; i < selGeos.length; i++) { if (selGeos[i] != geoPoint1) {
		 * if (selGeos[i] instanceof Transformable) {
		 * ret.addAll(Arrays.asList(kernel.Rotate(null, selGeos[i], num,
		 * geoPoint1))); } else if (selGeos[i].isGeoPolygon()) {
		 * ret.addAll(Arrays.asList(kernel.Rotate(null, selGeos[i], num,
		 * geoPoint1))); } } } if (!ret.isEmpty()) { app.storeUndoInfo();
		 * kernel.getApplication().getActiveEuclidianView()
		 * .getEuclidianController().memorizeJustCreatedGeos(ret); } return
		 * true; }
		 * 
		 * return false;
		 */
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(this, true);
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
