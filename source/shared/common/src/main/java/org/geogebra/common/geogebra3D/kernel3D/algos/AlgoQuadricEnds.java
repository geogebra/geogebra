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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimitedOrPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Compute the ends of a limited quadric
 *
 * @author mathieu
 */
public class AlgoQuadricEnds extends AlgoElement3D {

	private GeoQuadricND quadric; // input
	private GeoConic3D[] sections; // output
	private CoordSys coordsys1;
	private CoordSys coordsys2;

	private CoordMatrix pm = new CoordMatrix(4, 3);
	private CoordMatrix pmt = new CoordMatrix(3, 4);
	private Coords o1 = new Coords(3);
	private Coords o2 = new Coords(3);
	private Coords v = new Coords(3);
	private Coords vn1 = new Coords(3);
	private Coords vn2 = new Coords(3);

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
			LabelManager.setLabels(labels[0], sections);
		} else {
			LabelManager.setLabels(labels, sections);
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
		this(cons, quadric, false);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param quadric
	 *            quadric
	 * @param helper
	 *            whether this is just a helper
	 */
	public AlgoQuadricEnds(Construction cons, GeoQuadricND quadric,
			boolean helper) {
		super(cons, !helper);

		// set origin w coord
		pm.set(4, 3, 1);

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

		// if helper, will be updated by caller
		if (!helper) {
			setInputOutput(new GeoElement[] { quadric }, sections);
		}

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

	@Override
	public final void compute() {

		if (!quadric.isDefined()) {
			sections[0].setUndefined();
			sections[1].setUndefined();
			return;
		}

		sections[0].setDefined();
		sections[1].setDefined();

		o1.setAdd3(quadric.getMidpoint3D(), o1.setMul3(quadric.getEigenvec3D(2),
				((GeoQuadric3DLimitedOrPart) quadric).getBottomParameter()));
		o2.setAdd3(quadric.getMidpoint3D(), o2.setMul3(quadric.getEigenvec3D(2),
				((GeoQuadric3DLimitedOrPart) quadric).getTopParameter()));

		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_CYLINDER
				|| quadric.getType() == GeoQuadricNDConstants.QUADRIC_CONE) {
			if (DoubleUtil.isZero(quadric.getHalfAxis(0))
					&& (Double.isNaN(quadric.getHalfAxis(1))
							|| DoubleUtil.isZero(quadric.getHalfAxis(1)))) {
				// cylinder or cone equal to a segment
				sections[0].setSinglePointAtCoords(o1);
				sections[1].setSinglePointAtCoords(o2);
				return;
			}
		}

		CoordMatrix qm = quadric.getSymmetricMatrix();

		pm.setOrigin(o1);
		v.setSub3(o2, o1);
		v.completeOrthonormal3(vn1, vn2);
		pm.setVx(vn1);
		pm.setVy(vn2);
		pm.transposeCopy(pmt);

		// sets the conic matrix from plane and quadric matrix
		CoordMatrix cm = pmt.mul(qm).mul(pm);

		// Log.debug("pm=\n" + pm + "\nqm=\n" + qm + "\ncm=\n" + cm);

		coordsys1.resetCoordSys();
		coordsys1.addPoint(o1);
		coordsys1.addVector(vn1);
		coordsys1.addVector(v.setMul3(vn2, -1)); // orientation out of the
													// quadric
		coordsys1.makeOrthoMatrix(false, false);

		sections[0].setMatrix(cm);

		// section2
		pm.setOrigin(o2);
		pm.transposeCopy(pmt);

		cm = pmt.mul(qm).mul(pm);

		coordsys2.resetCoordSys();
		coordsys2.addPoint(o2);
		coordsys2.addVector(vn1);
		coordsys2.addVector(vn2);
		coordsys2.makeOrthoMatrix(false, false);

		sections[1].setMatrix(cm);

	}

	@Override
	public Commands getClassName() {
		return Commands.Ends;
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		quadric.remove();
	}

	/*
	 * final public String toString() { return
	 * loc.getPlain("EndsOfABetweenBC",((GeoElement)
	 * quadric).getLabel(),point.getLabel(),pointThrough.getLabel()); }
	 */

}
