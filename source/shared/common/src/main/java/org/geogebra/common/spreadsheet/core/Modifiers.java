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
	public static final Modifiers SHIFT = new Modifiers(false, false, true, false);

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
