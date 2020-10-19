package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

/**
 * Circle or sphere dialog
 *
 */
public abstract class InputDialogRadiusW extends ComponentInputDialog {

	/** current kernel */
	protected Kernel kernel;

	/**
	 * 
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param kernel
	 *            kernel
	 */
	public InputDialogRadiusW(AppW app, DialogData data, InputHandler handler,
			Kernel kernel) {
		super(app, data, false, false, handler, "Radius",
				"", 1, -1, false);
		this.kernel = kernel;
	}

	@Override
	protected void toolAction() {
		GeoElement circle = createOutput(getNumber());
		GeoElement[] geos = { circle };
		app.storeUndoInfoAndStateForModeStarting();
		kernel.getApplication().getActiveEuclidianView()
				.getEuclidianController().memorizeJustCreatedGeos(geos);
	}

	/**
	 * @return input as number
	 */
	protected GeoNumberValue getNumber() {
		return ((NumberInputHandler) getInputHandler()).getNum();
	}

	/**
	 * 
	 * @param num
	 *            radius value
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(GeoNumberValue num);
}