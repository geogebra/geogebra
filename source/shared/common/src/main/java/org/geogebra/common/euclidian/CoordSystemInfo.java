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

public class CoordSystemInfo {
	private final EuclidianView view;
	private boolean xAxisZoom = false;
	private boolean centerView;
	private boolean interactive = false;

	public CoordSystemInfo(EuclidianView view) {
		this.view = view;
	}

	private double deltaX() {
		return view.xZero - view.xZeroOld;
	}

	private double deltaY() {
		return view.yZero - view.yZeroOld;
	}

	public boolean isXAxisZoom() {
		return xAxisZoom;
	}

	public void setXAxisZoom(boolean xAxisZoom) {
		this.xAxisZoom = xAxisZoom;
	}

	@Override
	public String toString() {
		return "CoordSystemInfo{"
				+ "dx: " + deltaX()
				+ ", dy: " + deltaY()
				+ ", axisZoom: " + isXAxisZoom()
				+ '}';
	}

	public boolean isCenterView() {
		return centerView;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public void setCenterView(boolean value) {
		this.centerView = value;
	}

	public void setInteractive(boolean b) {
		this.interactive = b;
	}
}
