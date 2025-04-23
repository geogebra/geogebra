/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Input handler.
 */
public interface InputHandler {
	/**
	 * Processes inputString and returns success state.
	 */
	public void processInput(String inputString, ErrorHandler handler,
			AsyncOperation<Boolean> callback);
}
