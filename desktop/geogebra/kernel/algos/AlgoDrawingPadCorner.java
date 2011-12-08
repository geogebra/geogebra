/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

// adapted from AlgoTextCorner by Michael Borcherds 2008-05-10

package geogebra.kernel.algos;

import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.main.AppletImplementation;
import geogebra.main.Application;

import javax.swing.JPanel;

public class AlgoDrawingPadCorner extends AlgoElement implements
		EuclidianViewCE {

	private GeoPoint2 corner; // output
	private NumberValue number, evNum;

	public AlgoDrawingPadCorner(Construction cons, String label, NumberValue number,
			NumberValue evNum) {
		super(cons);
		this.number = number;
		this.evNum = evNum; // can be null

		corner = new GeoPoint2(cons);
		setInputOutput(); // for AlgoElement
		compute();
		corner.setEuclidianVisible(false); // hidden by default
		corner.setLabel(label);

		cons.registerEuclidianViewCE(this);

	}

	@Override
	public String getClassName() {
		return "AlgoDrawingPadCorner";
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evNum == null) {
			input = new GeoElement[1];
			input[0] = (GeoElement)number.toGeoElement();
		} else {
			input = new GeoElement[2];
			input[0] = (GeoElement)evNum.toGeoElement();
			input[1] = (GeoElement)number.toGeoElement();

		}

		super.setOutputLength(1);
		super.setOutput(0, corner);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint2 getCorner() {
		return corner;
	}

	@Override
	public final void compute() {

		// x1 = x1 / invXscale + xZero;
		// x2 = x2 / invXscale + xZero;

		EuclidianView ev;

		Application app = (Application)cons.getApplication();

		if (evNum == null || evNum.getDouble() == 1.0)
			ev = app.getEuclidianView();
		else {
			if (!app.hasEuclidianView2()) {
				corner.setUndefined();
				return;
			} else
				ev = ((Application) cons.getApplication()).getEuclidianView2();
		}

		double width = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		double height = ev.toRealWorldCoordY((double) (ev.getHeight()) + 1);
		double zeroX = ev.toRealWorldCoordX(-1);
		double zeroY = ev.toRealWorldCoordY(0 - 1);

		switch ((int) number.getDouble()) {
		case 1:
			corner.setCoords(zeroX, height, 1.0);
			break;
		case 2:
			corner.setCoords(width, height, 1.0);
			break;
		case 3:
			corner.setCoords(width, zeroY, 1.0);
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
			if (app.isApplet()) {
				AppletImplementation applet = app.getApplet();
				corner.setCoords(applet.width, applet.height, 1.0);
			} else {
				JPanel appCP = app.getCenterPanel();
				corner.setCoords(appCP.getWidth(), appCP.getHeight(), 1.0);
			}
			break;
		default:
			corner.setUndefined();
			break;
		}
	}

	final public static boolean wantsEuclidianViewUpdate() {
		return true;
	}

	@Override
	public final boolean euclidianViewUpdate() {
		compute();

		// update output:
		corner.updateCascade();
		return false;
	}

	@Override
	final public String toString() {
		return getCommandDescription();
	}

}
