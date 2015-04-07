/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimitedOrPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Compute the ends of a limited quadric
 *
 * @author mathieu
 */
public class AlgoQuadricEnds extends AlgoElement3D {

	private GeoQuadricND quadric; // input
	private GeoConic3D[] sections; // output
	private CoordSys coordsys1, coordsys2;

	/**
	 * 
	 * @param labels
	 *            labels
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEnds(Construction cons, String[] labels,
			GeoQuadricND quadric) {

		this(cons, quadric);

		if (labels == null) {
			sections[0].setLabel(null);
			sections[1].setLabel(null);
		} else if (labels.length == 1) {
			GeoElement.setLabels(labels[0], sections);
		} else {
			GeoElement.setLabels(labels, sections);
		}
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEnds(Construction cons, GeoQuadricND quadric) {
		super(cons);

		this.quadric = quadric;

		sections = new GeoConic3D[2];

		sections[0] = new GeoConic3D(cons);
		coordsys1 = new CoordSys(2);
		sections[0].setCoordSys(coordsys1);
		sections[0].setIsEndOfQuadric(true);
		sections[1] = new GeoConic3D(cons);
		coordsys2 = new CoordSys(2);
		sections[1].setCoordSys(coordsys2);
		sections[1].setIsEndOfQuadric(true);

		setInputOutput(new GeoElement[] { (GeoElement) quadric }, sections);

		compute();

	}

	public GeoConic3D getSection1() {
		return sections[0];
	}

	public GeoConic3D getSection2() {
		return sections[1];
	}

	public GeoConic3D[] getSections() {
		return sections;
	}

	private CoordMatrix pm = new CoordMatrix(4, 3);
	private CoordMatrix pmt = new CoordMatrix(3, 4);

	@Override
	public final void compute() {

		if (!quadric.isDefined()) {
			sections[0].setUndefined();
			sections[1].setUndefined();
			return;
		}

		sections[0].setDefined();
		sections[1].setDefined();

		CoordMatrix qm = quadric.getSymetricMatrix();
		Coords o1 = quadric.getMidpoint3D().add(
				quadric.getEigenvec3D(2).mul(
						((GeoQuadric3DLimitedOrPart) quadric)
								.getBottomParameter()));// point.getInhomCoordsInD3();
		Coords o2 = quadric.getMidpoint3D().add(
				quadric.getEigenvec3D(2)
						.mul(((GeoQuadric3DLimitedOrPart) quadric)
								.getTopParameter()));// pointThrough.getInhomCoordsInD3();
		pm.setOrigin(o1);
		Coords[] v = o2.sub(o1).completeOrthonormal();
		pm.setVx(v[0]);
		pm.setVy(v[1]);
		pm.transposeCopy(pmt);

		// sets the conic matrix from plane and quadric matrix
		CoordMatrix cm = pmt.mul(qm).mul(pm);

		// Application.debug("pm=\n"+pm+"\nqm=\n"+qm+"\ncm=\n"+cm);

		coordsys1.resetCoordSys();
		coordsys1.addPoint(o1);
		coordsys1.addVector(v[0]);
		coordsys1.addVector(v[1].mul(-1)); // orientation out of the quadric
		coordsys1.makeOrthoMatrix(false, false);

		sections[0].setMatrix(cm);

		// section2
		pm.setOrigin(o2);
		pm.transposeCopy(pmt);

		cm = pmt.mul(qm).mul(pm);

		coordsys2.resetCoordSys();
		coordsys2.addPoint(o2);
		coordsys2.addVector(v[0]);
		coordsys2.addVector(v[1]);
		coordsys2.makeOrthoMatrix(false, false);

		sections[1].setMatrix(cm);

		// areas
		sections[0].calcArea();
		sections[1].calcArea();

	}

	@Override
	public Commands getClassName() {
		return Commands.Ends;
	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		quadric.remove();
	}

	/*
	 * final public String toString() { return
	 * loc.getPlain("EndsOfABetweenBC",((GeoElement)
	 * quadric).getLabel(),point.getLabel(),pointThrough.getLabel()); }
	 */

	// TODO Consider locusequability
}
