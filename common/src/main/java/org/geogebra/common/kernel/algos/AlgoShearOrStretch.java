/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoApplyMatrix.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoShearOrStretch extends AlgoTransformation {

	private MatrixTransformable out;
	private GeoElement inGeo, outGeo;
	private GeoVec3D line;
	private GeoNumberValue num;
	private boolean shear;
	private double n;

	/**
	 * Creates new shear or stretch algorithm
	 * 
	 * @param cons
	 * @param in
	 * @param l
	 * @param num
	 * @param shear
	 *            shear if true, stretch otherwise
	 */
	public AlgoShearOrStretch(Construction cons, GeoElement in, GeoVec3D l,
			GeoNumberValue num, boolean shear) {
		super(cons);
		this.shear = shear;
		this.line = l;
		this.num = num;

		inGeo = in;
		if (inGeo instanceof GeoPoly || inGeo.isLimitedPath()) {
			outGeo = in.copyInternal(cons);
			out = (MatrixTransformable) outGeo;
		} else if (inGeo.isGeoList()) {
			outGeo = new GeoList(cons);
		} else if (inGeo instanceof GeoFunction) {
			out = new GeoCurveCartesian(cons);
			outGeo = (GeoElement) out;
		} else {
			out = (MatrixTransformable) inGeo.copy();
			outGeo = out.toGeoElement();
		}

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction())
			cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		if (shear)
			return Commands.Shear;
		return Commands.Stretch;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[num == null ? 2 : 3];
		input[0] = inGeo;
		input[1] = line;
		if (num != null)
			input[2] = num.toGeoElement();

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting element
	 * 
	 * @return resulting element
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo.isGeoFunction()) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else
			outGeo.set(inGeo);
		if (!outGeo.isDefined()) {
			return;
		}

		// matrix.add
		Translateable tranOut = (Translateable) out;

		double qx = 0.0d, qy = 0.0d, s, c;
		this.n = MyMath.length(line.x, line.y); // ;Math.sqrt(l.x*l.x+l.y*l.y);

		if (line instanceof GeoLine) {
			if (Math.abs(line.x) > Math.abs(line.y)) {
				qx = line.z / line.x;
			} else {
				qy = line.z / line.y;
			}
			s = -line.x / n;
			c = line.y / n;
			this.n = num.getDouble();
		} else {
			GeoPointND sp = ((GeoVector) line).getStartPoint();
			if (sp != null) {
				Coords qCoords = ((GeoVector) line).getStartPoint()
						.getCoordsInD2();
				qx = -qCoords.getX();
				qy = -qCoords.getY();
			}
			c = -line.y / n;
			s = line.x / n;
		}

		// translate -Q
		tranOut.translate(new Coords(qx, qy, 0));

		if (shear) {
			out.matrixTransform(1 - c * s * n, c * c * n, -s * s * n, 1 + s * c
					* n);
		} else {
			out.matrixTransform(c * c + s * s * n, c * s * (1 - n), c * s
					* (1 - n), s * s + c * c * n);
		}
		tranOut.translate(new Coords(-qx, -qy, 0));
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList))
			out = (MatrixTransformable) outGeo;

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction)
			return new GeoCurveCartesian(cons);
		return super.getResultTemplate(geo);
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart)) {
			super.transformLimitedPath(a, b);
		} else {
			super.transformLimitedConic(a, b);
		}
	}

	@Override
	public boolean swapOrientation(GeoConicPartND arc) {
		if (shear || num == null)
			return (arc == null || arc.positiveOrientation());
		return (arc == null || arc.positiveOrientation())
				^ (num.getDouble() < 0);
	}

	@Override
	public double getAreaScaleFactor() {

		if (shear) {
			return 1;
		}

		// else
		// stretch
		return n;

	}

	

}
