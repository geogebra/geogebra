package org.geogebra.desktop.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

public class InputDialogRegularPolygonD extends InputDialogD {
	private final GeoPointND geoPoint1;
	private final GeoPointND geoPoint2;
	private final GeoCoordSys2D direction;
	private final EuclidianController ec;

	/**
	 * @param app application
	 * @param ec controller
	 * @param title title
	 * @param handler input handler
	 * @param point1 first point
	 * @param point2 second point
	 * @param direction direction for 3D case
	 */
	public InputDialogRegularPolygonD(AppD app, EuclidianController ec,
			String title, InputHandler handler, GeoPointND point1,
			GeoPointND point2, GeoCoordSys2D direction) {
		super(app, app.getLocalization().getMenu("Points"), title, "4", false,
				handler, true);

		geoPoint1 = point1;
		geoPoint2 = point2;
		this.direction = direction;

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
		DialogManager.makeRegularPolygon(app, ec, inputPanel.getText(),
				geoPoint1, geoPoint2, direction, this,
				ok -> setVisibleForTools(!ok));
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.resetCurrentSelectionListener();
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	@Override
	public void handleDialogVisibilityChange(boolean isVisible) {
		// nothing to do
	}
}
