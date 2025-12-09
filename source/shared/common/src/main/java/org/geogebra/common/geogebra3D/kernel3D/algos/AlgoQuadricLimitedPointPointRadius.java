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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoRadius;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for cylinder between two end points and given radius. TODO implements
 * AlgoTransformable ?
 * 
 * @author mathieu
 *
 */
public abstract class AlgoQuadricLimitedPointPointRadius extends AlgoElement3D {

	// input
	private GeoPointND origin;
	private GeoPointND secondPoint;
	private NumberValue radius;

	// output
	private GeoQuadric3DPart side;
	protected GeoConic3D bottom;
	protected GeoConic3D top;
	private GeoQuadric3DLimited quadric;

	private AlgoQuadricSide algoSide;
	private AlgoQuadricEnds algoEnds;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param origin
	 *            center of bottom
	 * @param secondPoint
	 *            second point
	 * @param r
	 *            radius
	 * @param type
	 *            quadric type
	 */
	public AlgoQuadricLimitedPointPointRadius(Construction c, String[] labels,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue r,
			int type) {
		super(c);

		this.origin = origin;
		this.secondPoint = secondPoint;
		this.radius = r;

		quadric = new GeoQuadric3DLimited(c, type);

		input = new GeoElement[] { (GeoElement) origin,
				(GeoElement) secondPoint, (GeoElement) r };

		origin.addAlgorithm(this);
		secondPoint.addAlgorithm(this);
		r.addAlgorithm(this);

		// parent of output
		quadric.setParentAlgorithm(this);
		cons.addToAlgorithmList(this);

		setQuadric();

		algoSide = new AlgoQuadricSide(cons, quadric, true, null);
		cons.removeFromConstructionList(algoSide);
		side = (GeoQuadric3DPart) algoSide.getQuadric();
		side.setParentAlgorithm(this);
		quadric.setSide(side);

		algoEnds = createEnds();
		bottom.setParentAlgorithm(this);
		top.setParentAlgorithm(this);
		quadric.setBottomTop(bottom, top);

        side.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
        bottom.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
        top.setAuxiliaryObject(Auxiliary.YES_DEFAULT);

		// output = new GeoElement[] {quadric,bottom,top,side};
		setOutput();

		quadric.initLabelsIncludingBottom(labels);
		quadric.updatePartsVisualStyle();

		// force update for side
		update();
	}

	/**
	 * sets the output
	 */
	abstract protected void setOutput();

	abstract protected AlgoQuadricEnds createEnds();

	final private void computeHelpers() {
		// side must be done before ends (for midpoint)
		algoSide.compute();
		algoEnds.compute();
	}

	private boolean setQuadric() {
		// check end points
		if (!origin.isDefined() || origin.isInfinite()
				|| !secondPoint.isDefined()
				|| secondPoint.isInfinite() || !radius.isDefined()) {
			getQuadric().setUndefined();
			return false;
		}

		Coords o = origin.getInhomCoordsInD3();
		Coords o2 = secondPoint.getInhomCoordsInD3();
		Coords d = o2.sub(o);

		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getQuadric().setUndefined();
			return false;
		}

		double r = radius.getDouble();

		d.calcNorm();
		double altitude = d.getNorm();

		quadric.setDefined();

		setQuadric(o, o2, d.mul(1 / altitude), r, 0, altitude);

		return true;
	}

	@Override
	public void compute() {

		if (!setQuadric()) {
			bottom.setUndefined();
			top.setUndefined();
			side.setUndefined();
			return;
		}

		computeHelpers();

		quadric.calcVolume();
	}

	abstract protected void setQuadric(Coords o1, Coords o2, Coords d, double r,
			double min, double max);

	public GeoQuadric3DLimited getQuadric() {
		return quadric;
	}

	// //////////////////////
	// ALGOTRANSFORMABLE
	// //////////////////////

	/**
	 * 
	 * @param labels
	 *            transformed labels
	 * @param p1
	 *            transformed first point
	 * @param p2
	 *            transformed second point
	 * @param r
	 *            transformed radius
	 * @return new algo for transformed inputs
	 */
	protected abstract AlgoElement getTransformedAlgo(String[] labels,
			GeoPointND p1, GeoPointND p2, GeoNumeric r);

	/**
	 * @param t
	 *            transform
	 * @return transformed output
	 */
	public GeoElement[] getTransformedOutput(Transform t) {

		GeoPointND p1 = (GeoPointND) t.transform(origin,
				Transform.transformedGeoLabel(origin))[0];
		GeoPointND p2 = (GeoPointND) t.transform(secondPoint,
				Transform.transformedGeoLabel(secondPoint))[0];
		Transform.setVisualStyleForTransformations((GeoElement) origin,
				(GeoElement) p1);
		Transform.setVisualStyleForTransformations((GeoElement) secondPoint,
				(GeoElement) p2);

		GeoNumeric r = new AlgoRadius(this.cons,
				getQuadric().getBottom()).getRadius();
		r.setLabel(null);
		r.setAuxiliaryObject(true);

		GeoElement[] output = getOutput();
		String[] labels = new String[output.length];
		for (int i = 0; i < output.length; i++) {
			labels[i] = Transform.transformedGeoLabel(output[i]);
		}

		AlgoElement algo = getTransformedAlgo(labels, p1, p2, r);

		GeoElement[] ret = algo.getOutput();
		for (int i = 0; i < ret.length; i++) {
			Transform.setVisualStyleForTransformations(output[i], ret[i]);
		}

		algo.update();

		return ret;
	}

	// @Override
	// public void update() {
	//
	// if (stopUpdateCascade) {
	// return;
	// }
	//
	// compute();
	// quadric.update();
	//
	// if (!getQuadric().isLabelSet()) { // geo is in sequence/list : update
	// // bottom, top and side
	// getQuadric().getBottom().getParentAlgorithm().update();
	// getQuadric().getTop().getParentAlgorithm().update();
	// getQuadric().getSide().getParentAlgorithm().update();
	// }
	//
	// }

}
