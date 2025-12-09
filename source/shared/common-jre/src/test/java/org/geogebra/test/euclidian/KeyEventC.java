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
 
package org.geogebra.test.euclidian;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.editor.share.util.KeyCodes;

final class KeyEventC extends KeyEvent {
	private KeyCodes keyCode;

	public KeyEventC(KeyCodes keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public boolean isEnterKey() {
		return keyCode == KeyCodes.ENTER;
	}

	@Override
	public boolean isCtrlDown() {
		return false;
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public char getCharCode() {
		return (char) keyCode.getJavaKeyCode();
	}

	@Override
	public void preventDefault() {
		// nothing to do
	}
}