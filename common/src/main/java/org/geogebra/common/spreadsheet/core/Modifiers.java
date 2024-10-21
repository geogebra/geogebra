package org.geogebra.common.spreadsheet.core;

public final class Modifiers {
	/**
	 * Whether alt is pressed (same on all platforms)
	 */
	public final boolean alt;
	/**
	 * Flag for Ctrl on Windows, Cmd on Mac.
	 * Used for keyboard shortcuts like Ctrl/Cmd+C and multi-selection with mouse
	 */
	public final boolean ctrlOrCmd;

	/**
	 * Whether Shift is pressed (same on all platforms)
	 */
	public final boolean shift;

	/**
	 * Flag for secondary button click; covers right-click (all platforms) and Ctrl+Click on Mac
	 */
	public final boolean secondaryButton;

	public static final Modifiers NONE = new Modifiers(false, false, false, false);

	/**
	 * @param alt alt pressed?
	 * @param ctrlOrCmd ctrl (or cmd on Mac) pressed?
	 * @param secondaryButton right mouse button pressed?
	 */
	public Modifiers(boolean alt, boolean ctrlOrCmd, boolean shift, boolean secondaryButton) {
		this.alt = alt;
		this.ctrlOrCmd = ctrlOrCmd;
		this.shift = shift;
		this.secondaryButton = secondaryButton;
	}
}
