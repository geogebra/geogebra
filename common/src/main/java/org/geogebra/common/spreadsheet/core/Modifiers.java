package org.geogebra.common.spreadsheet.core;

public final class Modifiers {
	public final boolean alt;
	public final boolean ctrl;
	public final boolean shift;
	public final boolean rightButton;

	public static final Modifiers NONE = new Modifiers(false, false, false, false);

	/**
	 * @param alt alt pressed?
	 * @param ctrl ctrl (or cmd on Mac) pressed?
	 * @param rightButton right mouse button pressed?
	 */
	public Modifiers(boolean alt, boolean ctrl, boolean shift, boolean rightButton) {
		this.alt = alt;
		this.ctrl = ctrl;
		this.shift = shift;
		this.rightButton = rightButton;
	}
}
