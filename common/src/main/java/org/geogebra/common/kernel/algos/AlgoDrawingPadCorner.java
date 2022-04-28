/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

// adapted from AlgoTextCorner by Michael Borcherds 2008-05-10

package org.geogebra.common.kernel.algos;

import java.util.ConcurrentModificationException;

import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public class AlgoDrawingPadCorner extends AlgoElement {

	/** index for view direction corner */
	static public final int CORNER_VIEW_DIRECTION = 11;
	/** index for screen left-to-right direction corner */
	static public final int CORNER_SCREEN_RIGHT = 12;
	/** index for x/y/z axes scales */
	static public final int CORNER_AXES_SCALE = 13;

	protected GeoPointND corner; // output
	protected GeoNumberValue number;
	protected GeoNumberValue evNum;

	protected AlgoDrawingPadCorner(Construction cons, String label,
			GeoNumberValue number, GeoNumberValue evNum, double absCorner) {
		super(cons);
		this.number = number;
		this.evNum = evNum; // can be null

		corner = newGeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
		corner.setEuclidianVisible(false); // hidden by default
		corner.setLabel(label);

		registerEV(absCorner);
	}

	private void registerEV(double absCorner) {
		cons.registerEuclidianViewCE(this);
		Double d = number.getDouble();
		if (DoubleUtil.isEqual(d, CORNER_VIEW_DIRECTION)
				|| DoubleUtil.isEqual(d, CORNER_SCREEN_RIGHT)) {
			cons.registerCorner11(this);
			return;
		}
		if (DoubleUtil.isEqual(d, absCorner)) {
			cons.registerCorner5(this);
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param number
	 *            corner index
	 * @param evNum
	 *            view number
	 * @param absCorner
	 *            index of abs corner in active view
	 */
	public AlgoDrawingPadCorner(Construction cons, GeoNumberValue number,
			GeoNumberValue evNum, double absCorner) {
		super(cons);
		this.number = number;
		this.evNum = evNum; // can be null

		corner = newGeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
		corner.setEuclidianVisible(false); // hidden by default

		registerEV(absCorner);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param number
	 *            corner index
	 * @param evNum
	 *            view number
	 */
	public AlgoDrawingPadCorner(Construction cons, String label,
			GeoNumberValue number, GeoNumberValue evNum) {
		this(cons, label, number, evNum, 5);
	}

	/**
	 * @param cons1
	 *            construction
	 * @return new point
	 */
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

	@Override
	public Commands getClassName() {
		return Commands.Corner;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evNum == null) {
			input = new GeoElement[1];
			input[0] = number.toGeoElement();
		} else {
			input = new GeoElement[2];
			input[0] = evNum.toGeoElement();
			input[1] = number.toGeoElement();

		}

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) corner);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return corner
	 */
	public GeoPointND getCorner() {
		return corner;
	}

	@Override
	public void compute() {

		// x1 = x1 / invXscale + xZero;
		// x2 = x2 / invXscale + xZero;

		EuclidianViewInterfaceSlim ev;

		App app = cons.getApplication();

		if (evNum == null || evNum.getDouble() == 1.0) {
			ev = app.getEuclidianView1();
		} else {
			if (!app.hasEuclidianView2(1)) {
				try {
					corner.setUndefined();
				} catch (ConcurrentModificationException e) {
					Log.error("problem with Corner()" + e.getMessage());
				}
				return;
			}
			ev = app.getEuclidianView2(1);
		}

		double xmax = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		double ymin = ev.toRealWorldCoordY((double) (ev.getHeight()) + 1);
		double zeroX = ev.toRealWorldCoordX(-1);
		double zeroY = ev.toRealWorldCoordY(0 - 1);

		switch ((int) number.getDouble()) {
		case 1:
			corner.setCoords(zeroX, ymin, 1.0);
			break;
		case 2:
			corner.setCoords(xmax, ymin, 1.0);
			break;
		case 3:
			corner.setCoords(xmax, zeroY, 1.0);
			break;
		case 4:
			corner.setCoords(zeroX, zeroY, 1.0);
			break;
		case 5: // return size of Graphics View in pixels
			corner.setCoords(getWidth(ev), getHeight(ev), 1.0);
			break;
		case 6: // return size of Window in pixels
			// (to help with sizing for export to applet)
			// doesn't work very well as it receives updates only when
			// EuclidianView is changed
			corner.setCoords(app.getWidth(), app.getHeight(), 1.0);

			break;
		default:
			corner.setUndefined();
			break;
		}
	}

	private double getWidth(EuclidianViewInterfaceSlim ev) {
		int width = ev.getWidth();
		return width >= 1 || ev.getSettings() == null ? width : ev.getSettings().getFileWidth();
	}

	private double getHeight(EuclidianViewInterfaceSlim ev) {
		int height = ev.getHeight();
		return height >= 1 || ev.getSettings() == null ? height : ev.getSettings().getFileHeight();
	}

	@Override
	public final boolean euclidianViewUpdate() {
		compute();

		// update output:
		corner.updateCascade();
		return false;
	}

}
