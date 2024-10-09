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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * 
 * @author Markus
 */
public class AlgoApplyMatrix extends AlgoTransformation {

	private MatrixTransformable out;
	private final GeoList matrix;

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input geo
	 * @param matrix
	 *            transform matrix
	 */
	public AlgoApplyMatrix(Construction cons, GeoElement in, GeoList matrix) {
		super(cons);

		this.matrix = matrix;

		inGeo = in.toGeoElement();
		if ((inGeo instanceof GeoPoly) || inGeo.isLimitedPath()) {
			outGeo = in.copyInternal(cons);
			out = (MatrixTransformable) outGeo;
		} else if (inGeo.isGeoList()) {
			outGeo = new GeoList(cons);
		} else if (inGeo instanceof GeoFunction) {
			out = new GeoCurveCartesian(cons);
			outGeo = out.toGeoElement();
		} else {
			out = (MatrixTransformable) inGeo.copy();
			outGeo = out.toGeoElement();
		}

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction()) {
			cons.registerEuclidianViewCE(this);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.ApplyMatrix;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[1] = inGeo;
		input[0] = matrix;

		setOnlyOutput(outGeo);
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
		} else {
			setOutGeo();
		}

		if (!outGeo.isDefined()) {
			return;
		}

		MyList list = matrix.getMyList();

		if ((list.getMatrixCols() != list.getMatrixRows())
				|| (list.getMatrixRows() < 2) || (list.getMatrixRows() > 3)) {
			outGeo.setUndefined();
			return;
		}

		double a, b, c, d, e, f, g, h, i;
		if (list.getMatrixRows() < 3) {
			a = MyList.getCellAsDouble(list, 0, 0);
			b = MyList.getCellAsDouble(list, 1, 0);
			c = MyList.getCellAsDouble(list, 0, 1);
			d = MyList.getCellAsDouble(list, 1, 1);
			out.matrixTransform(a, b, c, d);
		} else {
			a = MyList.getCellAsDouble(list, 0, 0);
			b = MyList.getCellAsDouble(list, 1, 0);
			c = MyList.getCellAsDouble(list, 2, 0);
			d = MyList.getCellAsDouble(list, 0, 1);
			e = MyList.getCellAsDouble(list, 1, 1);
			f = MyList.getCellAsDouble(list, 2, 1);
			g = MyList.getCellAsDouble(list, 0, 2);
			h = MyList.getCellAsDouble(list, 1, 2);
			i = MyList.getCellAsDouble(list, 2, 2);
			out.matrixTransform(a, b, c, d, e, f, g, h, i);
		}
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(out instanceof GeoList)
				&& (outGeo instanceof MatrixTransformable)) {
			out = (MatrixTransformable) outGeo;
		}

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction) {
			return new GeoCurveCartesian(cons);
		}
		return super.getResultTemplate(geo);
	}

	@Override
	public boolean swapOrientation(GeoConicPartND arc) {
		double a, b, c, d;
		a = matrix.get(0, 0).evaluateDouble();
		b = matrix.get(1, 0).evaluateDouble();
		c = matrix.get(0, 1).evaluateDouble();
		d = matrix.get(1, 1).evaluateDouble();
		return (arc == null || arc.positiveOrientation())
				^ (((a * d) - (b * c)) < 0);
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
	public double getAreaScaleFactor() {
		double a = matrix.get(0, 0).evaluateDouble();
		double b = matrix.get(1, 0).evaluateDouble();
		double c = matrix.get(0, 1).evaluateDouble();
		double d = matrix.get(1, 1).evaluateDouble();
		return (a * d) - (b * c);
	}

}
