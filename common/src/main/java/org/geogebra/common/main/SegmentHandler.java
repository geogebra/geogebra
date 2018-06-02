package org.geogebra.common.main;

import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class SegmentHandler {
	private GeoPointND point;
	private Kernel kernel;

	public SegmentHandler(GeoPointND geoPoint2, Kernel kernel) {
		this.point = geoPoint2;
		this.kernel = kernel;
	}

	/**
	 * @param text
	 *            input
	 * @param inputHandler
	 *            text to number convertor
	 * @param eh
	 *            error handler
	 * @param callback
	 *            success callback
	 */
	public void doSegmentFixedAsync(String text,
			final NumberInputHandler inputHandler, ErrorHandler eh,
			final AsyncOperation<Boolean> callback) {
		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		inputHandler.processInput(text, eh, new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean ok) {
				cons.setSuppressLabelCreation(oldVal);
				if (ok) {
					DialogManager.doSegmentFixed(kernel, point,
							inputHandler.getNum());
				}
				callback.callback(ok);
			}
		});

	}

}
