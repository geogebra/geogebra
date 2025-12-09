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

package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

class InputBoxErrorHandler implements ErrorHandler {

	boolean errorOccurred;

	@Override
	public void showError(String msg) {
		errorOccurred = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccurred = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccurred = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccurred = false;
	}
}
