package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.DialogManager;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

public class InputDialogDilate extends InputDialogD {

	GeoPoint[] points;
	GeoElement[] selGeos;

	private Kernel kernel;

	public InputDialogDilate(AppD app, String title, InputHandler handler,
			GeoPoint[] points, GeoElement[] selGeos, Kernel kernel) {
		super(app.getFrame(), false);

		this.app = app;
		inputHandler = handler;

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		createGUI(title, app.getMenu("Dilate.Factor"), false, DEFAULT_COLUMNS,
				1, true, false, false, false, DialogType.GeoGebraEditor);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();
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

		boolean success = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);

		if (success) {
			return DialogManager.doDilate(kernel,
					((NumberInputHandler) inputHandler).getNum(), points,
					selGeos);
		}

		return false;

		/*
		 * 
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * boolean success = inputHandler.processInput(inputPanel.getText());
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (success) { NumberValue num =
		 * ((NumberInputHandler)inputHandler).getNum();
		 * 
		 * if (selGeos.length > 0) { // mirror all selected geos //GeoElement []
		 * selGeos = getSelectedGeos(); GeoPoint2 point = points[0];
		 * ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for (int
		 * i=0; i < selGeos.length; i++) { if (selGeos[i] != point) { if
		 * ((selGeos[i] instanceof Transformable) || selGeos[i].isGeoList())
		 * ret.addAll(Arrays.asList(kernel.Dilate(null, selGeos[i], num,
		 * point))); } } if (!ret.isEmpty()) {
		 * kernel.getApplication().getActiveEuclidianView
		 * ().getEuclidianController().memorizeJustCreatedGeos(ret);
		 * app.storeUndoInfo(); } return true; } } return false;
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
		// TODO Auto-generated method stub
	}

}
