package org.geogebra.web.full.gui.laf;

/**
 * LAF for offline chrome apps
 *
 */
public class ChromeLookAndFeel extends GLookAndFeel {

	@Override
	public boolean isGraphingExamSupported() {
		return true;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public boolean examSupported() {
		return true;
	}

}
