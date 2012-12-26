/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.discrete;

import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GPoint2D.Double;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.GraphAlgo;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.List;

/**
 * Mode of a list. Adapted from AlgoMode
 * 
 * @author Michael Borcherds

 */

public class AlgoConvexHull extends AlgoElement  implements GraphAlgo {

	
	private GeoList inputList; // input
	private GeoLocus locus; // output
	private ArrayList<MyPoint> al;
	private ArrayList<Double> vl;
	private int size;

	/**
	 * @param cons cons
	 * @param label label
	 * @param inputList list of GeoPoints
	 */
	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);
	}

	@Override
	public Commands getClassName() {
        return Commands.ConvexHull;
    }

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return convex hull as a GeoLocus
	 */
	public GeoLocus getResult() {
		return locus;
	}

	@Override
	public void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		if (vl == null)
			vl = new ArrayList<GPoint2D.Double>();
		else
			vl.clear();

		double inhom[] = new double[2];



		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);
				
				// make sure duplicates aren't added
				if (!contains(vl, inhom[0], inhom[1])) {
					vl.add(new GPoint2D.Double(inhom[0], inhom[1]));
				}
			}
		}
		
		if (al == null) {
			al = new ArrayList<MyPoint>();
		} else {
			al.clear();
		}

		if (vl.size() == 1) {
			MyPoint p = al.get(0);
			al.add(new MyPoint(p.x, p.y, false));
			al.add(new MyPoint(p.x, p.y, true));
			locus.setPoints(al);
			locus.setDefined(true);
			return;
		}


		List<GPoint2D.Double> edge = JarvisMarch2D.convexHull(vl);
		 
		
		for (int i = 0; i < edge.size(); i++) {
			GPoint2D.Double p = edge.get(i);
			al.add(new MyPoint(p.x, p.y, i != 0));

		}
		
		// close the polygon
		GPoint2D.Double p = edge.get(0);
		al.add(new MyPoint(p.x, p.y, true));


		locus.setPoints(al);
		locus.setDefined(true);

	}

	private static boolean contains(ArrayList<Double> vl2, double x, double y) {
		for (int i = 0 ; i < vl2.size() ; i++) {
			GPoint2D.Double p = vl2.get(i);
			if (Kernel.isEqual(p.x, x) && Kernel.isEqual(p.y, y)) {
				return true;
			}
		}
		
		return false;
	}

	// TODO Consider locusequability

}
