package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.dialog.handler.SegmentHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

/**
 * Dialog for segment with fixed radius.
 */
public class InputDialogSegmentFixedW extends ComponentInputDialog {
	private GeoPointND geoPoint1;
	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param point1
	 *            startpoint
	 * @param kernel
	 *            kernel
	 */
	public InputDialogSegmentFixedW(AppW app, DialogData data,
			InputHandler handler, GeoPointND point1, Kernel kernel) {
		super(app, data, false, false, handler, app.getLocalization().getMenu("Length"),
				"", 1, -1, false);
		this.kernel = kernel;
		geoPoint1 = point1;
	}

	@Override
	public void processInput() {
		new SegmentHandler(geoPoint1, kernel).doSegmentFixedAsync(
				getInputText(),
				(NumberInputHandler) getInputHandler(), this,
				ok -> {
					if (ok) {
						hide();
					}
				});
	}
}