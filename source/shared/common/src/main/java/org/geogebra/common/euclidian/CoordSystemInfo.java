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

package org.geogebra.common.euclidian;

/**
 * Describes one coordinate-system change in a Euclidian view.
 *
 * <p>The instance records the view context together with the main user-visible
 * properties of the change, such as whether it was centered, interactive, or
 * scaled along one axis.
 */
public class CoordSystemInfo {

	/**
	 * Identifies which axis was scaled by the coordinate-system change.
	 */
	public enum ScaledAxis {
		/** No single axis scaling is recorded. */
		NONE,
		/** The x-axis was scaled. */
		X_AXIS,
		/** The y-axis was scaled. */
		Y_AXIS,
		/** Both x- and y-axis was scaled. */
		BOTH
	}

	private final EuclidianView view;
	private ScaledAxis scaledAxis = ScaledAxis.NONE;
	private boolean centerView;
	private boolean interactive = false;

	/**
	 * Creates coordinate-system change information for the given view.
	 *
	 * @param view view whose coordinate-system change is described
	 */
	public CoordSystemInfo(EuclidianView view) {
		this.view = view;
	}

	private double deltaX() {
		return view.xZero - view.xZeroOld;
	}

	private double deltaY() {
		return view.yZero - view.yZeroOld;
	}

	@Override
	public String toString() {
		return "CoordSystemInfo{"
				+ "dx: " + deltaX()
				+ ", dy: " + deltaY()
				+ ", scaledAxis: " + scaledAxis
				+ ", interactive: " + isInteractive()
				+ '}';
	}

	/**
	 * Returns whether the change recenters the view.
	 *
	 * @return whether the view is centered by this change
	 */
	public boolean isCenterView() {
		return centerView;
	}

	/**
	 * Returns whether the change comes from an interactive action.
	 *
	 * @return whether the change is interactive
	 */
	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * Records whether the change recenters the view.
	 *
	 * @param value whether the view is centered by this change
	 */
	public void setCenterView(boolean value) {
		this.centerView = value;
	}

	/**
	 * Records whether the change comes from an interactive action.
	 *
	 * @param b whether the change is interactive
	 */
	public void setInteractive(boolean b) {
		this.interactive = b;
	}

	/**
	 * Records which axis was scaled by this change.
	 *
	 * @param scaledAxis scaled axis to record
	 */
	public void setScaledAxis(ScaledAxis scaledAxis) {
		this.scaledAxis = scaledAxis;
	}

	/**
	 * Clears any recorded single-axis scaling.
	 */
	public void cancelScaledAxis() {
		scaledAxis = ScaledAxis.NONE;
	}

	/**
	 * Returns whether this change records scaling of exactly one axis.
	 *
	 * @return whether a scaled axis is recorded
	 */
	public boolean hasScaledAxis() {
		return scaledAxis != ScaledAxis.NONE;
	}

}
