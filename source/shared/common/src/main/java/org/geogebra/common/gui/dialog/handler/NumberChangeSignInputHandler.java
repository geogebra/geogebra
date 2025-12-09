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

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Handler of a number, with possibility of changing the sign
 * 
 * @author mathieu
 * 
 */
public class NumberChangeSignInputHandler extends NumberInputHandler {
	public NumberChangeSignInputHandler(AlgebraProcessor algebraProcessor,
			AsyncOperation<GeoNumberValue> callback, App app) {
		super(algebraProcessor, callback, app);
	}

	/**
	 * If (changeSign==true), change sign of the number handled
	 * 
	 * @param inputString
	 *            input
	 * @param changeSign
	 *            whether sign change checkbox is checked
	 * @param handler
	 *            error handler
	 * @param callback
	 *            callback
	 */
	public void processInput(String inputString, boolean changeSign,
			ErrorHandler handler, AsyncOperation<Boolean> callback) {
		if (changeSign) {
			StringBuilder sb = new StringBuilder();
			sb.append("-(");
			sb.append(inputString);
			sb.append(")");
			processInput(sb.toString(), handler, callback);
		} else {

			processInput(inputString, handler, callback);
		}
	}
}
