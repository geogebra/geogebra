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

import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

public class AlgoDrawingPadCorner extends AlgoElement {

	protected GeoPointND corner; // output
	protected NumberValue number;
	protected NumberValue evNum;

	protected AlgoDrawingPadCorner(Construction cons, String label,
			NumberValue number, NumberValue evNum, double absCorner) {
		super(cons);
		this.number = number;
		this.evNum = evNum; // can be null

		corner = newGeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
		corner.setEuclidianVisible(false); // hidden by default
		corner.setLabel(label);

		cons.registerEuclidianViewCE(this);
		if (Kernel.isEqual(number.getDouble(), absCorner)) {
			cons.registerCorner5(this);
		}

	}

	public AlgoDrawingPadCorner(Construction cons, String label,
			NumberValue number, NumberValue evNum) {
		this(cons, label, number, evNum, 5);

	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new point
	 */
	protected GeoPointND newGeoPoint(Construction cons) {
		return new GeoPoint(cons);
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

	public GeoPointND getCorner() {
		return corner;
	}

	@Override
	public void compute() {

		// x1 = x1 / invXscale + xZero;
		// x2 = x2 / invXscale + xZero;

		EuclidianViewInterfaceSlim ev;

		App app = cons.getApplication();

		if (evNum == null || evNum.getDouble() == 1.0)
			ev = app.getEuclidianView1();
		else {
			if (!app.hasEuclidianView2(1)) {
				corner.setUndefined();
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
			corner.setCoords(ev.getWidth(), ev.getHeight(), 1.0);
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

	@Override
	public final boolean euclidianViewUpdate() {
		compute();

		// update output:
		corner.updateCascade();
		return false;
	}

	// TODO Consider locusequability

}
