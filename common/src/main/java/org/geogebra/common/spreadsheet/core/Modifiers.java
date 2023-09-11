package org.geogebra.common.spreadsheet.core;

public final class Modifiers {
	public final boolean alt;
	public final boolean ctrl;
	public final boolean rightButton;

	/**
	 * @param alt alt pressed?
	 * @param ctrl ctrl (or cmd on Mac) pressed?
	 * @param rightButton right mouse button pressed?
	 */
	public Modifiers(boolean alt, boolean ctrl, boolean rightButton) {
		this.alt = alt;
		this.ctrl = ctrl;
		this.rightButton = rightButton;
	}
}
