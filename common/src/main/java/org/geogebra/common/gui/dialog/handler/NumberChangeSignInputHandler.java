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
