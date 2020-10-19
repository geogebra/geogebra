package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

/**
 * Dialog for dilate tool
 */
public class InputDialogDilateW extends ComponentInputDialog {
	private GeoPointND[] points;
	private GeoElement[] selGeos;
	private Kernel kernel;
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog trans keys
	 * @param handler
	 *            value handler
	 * @param points
	 *            input points
	 * @param selGeos
	 *            transformed geos
	 * @param kernel
	 *            kernel
	 * @param ec
	 *            euclidian controller
	 */
	public InputDialogDilateW(AppW app, DialogData data, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu("Dilate.Factor"), "",
				1, -1, false);
		setInputHandler(handler);
		addStyleName("dilate");

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;	
		this.ec = ec;
	}

	/**
	 * Execute the dilation with input.
	 */
	@Override
	protected void toolAction() {
		DialogManager.doDilate(kernel,
				((NumberInputHandler) getInputHandler()).getNum(), points,
				selGeos, ec);
	}
}