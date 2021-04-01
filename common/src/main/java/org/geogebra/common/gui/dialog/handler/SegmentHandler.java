package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class SegmentHandler {
	private GeoPointND point;
	private Kernel kernel;

	/**
	 * @param geoPoint2
	 *            start point
	 * @param kernel
	 *            kernel
	 */
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
		inputHandler.processInput(text, eh, new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean ok) {
				if (ok) {
					DialogManager.doSegmentFixed(kernel, point,
							inputHandler.getNum());
				}
				callback.callback(ok);
			}
		});

	}

}
