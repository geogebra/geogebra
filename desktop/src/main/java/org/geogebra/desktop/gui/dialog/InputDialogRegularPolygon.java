package org.geogebra.desktop.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

public class InputDialogRegularPolygon extends InputDialogD {
	private GeoPointND geoPoint1, geoPoint2;
	private EuclidianController ec;

	public InputDialogRegularPolygon(AppD app, EuclidianController ec,
			String title, InputHandler handler, GeoPointND point1,
			GeoPointND point2) {
		super(app, app.getPlain("Points"), title, "4", false, handler, true);

		geoPoint1 = point1;
		geoPoint2 = point2;

		this.ec = ec;
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

		return DialogManager.makeRegularPolygon(app, ec, inputPanel.getText(),
				geoPoint1, geoPoint2);

		/*
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * boolean ret = inputHandler.processInput(inputPanel.getText());
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (ret) { GeoElement[] geos = kernel.RegularPolygon(null, geoPoint1,
		 * geoPoint2, ((NumberInputHandler)inputHandler).getNum()); GeoElement[]
		 * onlypoly = { null }; if (geos != null) { onlypoly[0] = geos[0];
		 * app.storeUndoInfo();
		 * kernel.getApplication().getActiveEuclidianView().
		 * getEuclidianController().memorizeJustCreatedGeos(onlypoly); } }
		 * 
		 * return ret;
		 */
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void handleDialogVisibilityChange(boolean isVisible) {

	}
}
