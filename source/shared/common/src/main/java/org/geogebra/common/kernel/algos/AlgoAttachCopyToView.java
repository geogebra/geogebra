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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Zbynek
 */
public class AlgoAttachCopyToView extends AlgoTransformation {

	private MatrixTransformable out;
	private GeoNumberValue viewID;
	private GeoPointND corner1;
	private GeoPointND corner3;
	private GeoPointND screenCorner1;
	private GeoPointND screenCorner3;

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param in
	 *            input element
	 * @param viewID
	 *            id of Euclidian view (0 = no,1 = EV1,2=EV2)
	 * @param corner1
	 *            first real world point
	 * @param corner3
	 *            second real world point
	 * @param screenCorner1
	 *            screen point corresponding to corner1
	 * @param screenCorner3
	 *            screen point corresponding to corner3
	 */
	public AlgoAttachCopyToView(Construction cons, String label,
			GeoElementND in, GeoNumberValue viewID, GeoPointND corner1,
			GeoPointND corner3, GeoPointND screenCorner1,
			GeoPointND screenCorner3) {
		this(cons, in, viewID, corner1, corner3, screenCorner1, screenCorner3);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input element
	 * @param viewID
	 *            id of Euclidian view (0 = no,1 = EV1,2=EV2)
	 * @param corner1
	 *            first real world point
	 * @param corner3
	 *            second real world point
	 * @param screenCorner1
	 *            screen point corresponding to corner1
	 * @param screenCorner3
	 *            screen point corresponding to corner3
	 */
	public AlgoAttachCopyToView(Construction cons, GeoElementND in,
			GeoNumberValue viewID, GeoPointND corner1, GeoPointND corner3,
			GeoPointND screenCorner1, GeoPointND screenCorner3) {
		super(cons);

		this.viewID = viewID;
		this.corner1 = corner1;
		this.corner3 = corner3;
		this.screenCorner1 = screenCorner1;
		this.screenCorner3 = screenCorner3;

		inGeo = in.toGeoElement();
		if ((inGeo instanceof GeoPoly) || inGeo.isLimitedPath()) {
			outGeo = in.copyInternal(cons);
			out = (MatrixTransformable) outGeo;
		} else if (inGeo.isGeoList()) {
			outGeo = new GeoList(cons);
		} else if (inGeo instanceof GeoFunction) {
			outGeo = inGeo.copy();
		} else {
			out = (MatrixTransformable) inGeo.copy();
			outGeo = out.toGeoElement();
		}

		setInputOutput();
		compute();
		cons.registerEuclidianViewCE(this);

	}

	@Override
	public Commands getClassName() {
		return Commands.AttachCopyToView;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[6];
		input[0] = inGeo;
		input[1] = viewID.toGeoElement();
		input[2] = corner1.toGeoElement();
		input[3] = corner3.toGeoElement();
		input[4] = screenCorner1.toGeoElement();
		input[5] = screenCorner3.toGeoElement();

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
		int view = (int) viewID.getDouble();

		// #5014
		// use Settings so we don't need to initialise EV2
		EuclidianSettings ev = null;
		if (view == 1 || view == 2) {
			ev = kernel.getApplication().getSettings().getEuclidian(view);
		}

		if (ev == null && view != 0) {
			outGeo.setUndefined();
			return;
		}
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (!inGeo.isGeoFunction()) {
			setOutGeo();
		}

		if (!outGeo.isDefined()) {
			return;
		}

		if (view == 0) {
			return;
		}

		Coords c1 = corner1.getCoordsInD3();
		Coords c3 = corner3.getCoordsInD3();
		Coords c5 = screenCorner1.getCoordsInD3();
		Coords c7 = screenCorner3.getCoordsInD3();

		double c1x = ev.toRealWorldCoordX(c5.getX());
		double c1y = ev.toRealWorldCoordY(c5.getY());
		double c3x = ev.toRealWorldCoordX(c7.getX());
		double c3y = ev.toRealWorldCoordY(c7.getY());
		double[][] m1 = MyMath.adjoint(c1.getX(), c1.getY(), 1, c3.getX(),
				c3.getY(), 1, c1.getX(), c3.getY(), 1);
		double[][] m2 = new double[][] { { c1x, c3x, c1x }, { c1y, c3y, c3y },
				{ 1, 1, 1 } };
		double[][] m = MyMath.multiply(m2, m1);
		if (!(inGeo instanceof GeoFunction)) {
			out.matrixTransform(m[0][0], m[0][1], m[0][2], m[1][0], m[1][1],
					m[1][2], m[2][0], m[2][1], m[2][2]);
			// TODO check why we need this when result has points on it

			outGeo.updateCascade();
		} else {
			transformFunction(m[0][0] / m[2][2], m[0][2] / m[2][2],
					m[1][1] / m[2][2], m[1][2] / m[2][2]);
		}
	}

	private void transformFunction(double d, double e, double f, double g) {
		Function fun = ((GeoFunction) inGeo).getFunction();
		ExpressionNode expr = fun.getExpression().getCopy(kernel);
		expr = expr.replace(fun.getFunctionVariable(),
				new ExpressionNode(kernel, fun.getFunctionVariable())
						.multiply(1 / d).plus(-e / d))
				.wrap();
		Function fun2 = new Function(expr.multiply(f).plus(g),
				fun.getFunctionVariable());
		((GeoFunction) outGeo).setFunction(fun2);
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
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart)) {
			super.transformLimitedPath(a, b);
		} else {
			super.transformLimitedConic(a, b);
		}

	}

	/**
	 * @param viewID2
	 *            new EV ID (1 or 2)
	 */
	public void setEV(int viewID2) {
		input[1].removeAlgorithm(this);
		viewID = new GeoNumeric(cons, viewID2);
		input[1] = viewID.toGeoElement();

	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

}
