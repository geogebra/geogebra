/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		inputHandler.processInput(text, eh, ok -> {
			if (ok) {
				DialogManager.doSegmentFixed(kernel, point,
						inputHandler.getNum());
			}
			callback.callback(ok);
		});

	}

}
