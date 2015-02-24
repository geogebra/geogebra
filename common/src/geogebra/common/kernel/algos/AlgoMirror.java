/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMirrorPointPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.ConicMirrorable;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.Mirrorable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoMirror extends AlgoTransformation implements
		RestrictionAlgoForLocusEquation {

	protected Mirrorable out;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	private GeoLineND mirrorLine;
	protected GeoPointND mirrorPoint;
	private GeoConic mirrorConic;
	protected GeoElement mirror;

	private GeoPoint transformedPoint;

	/**
	 * Creates new "mirror at point" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param p
	 */
	protected AlgoMirror(Construction cons, String label, GeoElement in,
			GeoPointND p) {

		this(cons, in, p);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at point" algo
	 * 
	 * @param cons
	 * @param in
	 * @param p
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoPointND p) {

		this(cons);
		mirrorPoint = p;
		endOfConstruction(cons, in, (GeoElement) p);
	}

	/**
	 * Creates new "mirror at conic" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param c
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoConic c) {

		this(cons, in, c);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at conic" algo
	 * 
	 * @param cons
	 * @param in
	 * @param c
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoConic c) {

		this(cons);
		mirrorConic = c;
		endOfConstruction(cons, in, c);
	}

	/**
	 * Creates new "mirror at line" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param g
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoLineND g) {

		this(cons, in, g);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at line" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param g
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoLineND g) {

		this(cons);
		mirrorLine = g;
		endOfConstruction(cons, in, (GeoElement) g);
	}

	/**
	 * used for 3D
	 * 
	 * @param cons
	 *            cons
	 */
	protected AlgoMirror(Construction cons) {
		super(cons);
	}

	/**
	 * end of construction
	 * 
	 * @param cons
	 *            cons
	 * @param in
	 *            transformed geo
	 * @param mirror
	 *            mirror
	 */
	public void endOfConstruction(Construction cons, GeoElement in,
			GeoElement mirror) {

		this.mirror = mirror;

		inGeo = in;
		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Mirrorable)
			out = (Mirrorable) outGeo;
		setInputOutput();

		cons.registerEuclidianViewCE(this);
		transformedPoint = new GeoPoint(cons);
		compute();
		if (inGeo.isGeoFunction())
			cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mirror;
	}

	@Override
	public int getRelatedModeID() {
		if (mirror.isGeoLine()) {
			return EuclidianConstants.MODE_MIRROR_AT_LINE;
		} else if (mirror.isGeoPoint()) {
			return EuclidianConstants.MODE_MIRROR_AT_POINT;
		} else {
			return EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
		}

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inGeo;
		input[1] = mirror;

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the transformed geo
	 * 
	 * @return transformed geo
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	public final void compute() {

		if (!mirror.isDefined()) {
			outGeo.setUndefined();
			return;
		}

		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}

		setOutGeo();
		if (!outGeo.isDefined()) {
			return;
		}

		if (inGeo.isRegion() && mirror == mirrorConic) {
			GeoVec2D v = mirrorConic.getTranslationVector();
			outGeo.setInverseFill(((Region) inGeo).isInRegion(v.getX(),
					v.getY())
					^ inGeo.isInverseFill());
		}

		computeRegardingMirror();

		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	/**
	 * compute regarding which mirror type is used
	 */
	protected void computeRegardingMirror() {
		if (mirror == mirrorLine) {
			if (mirrorLine.getStartPoint() == null) {
				mirrorLine.setStandardStartPoint();
			}
			out.mirror(mirrorLine);
		} else if (mirror == mirrorPoint) {
			if (outGeo.isGeoFunction()) {
				((GeoFunction) outGeo).mirror(getMirrorCoords());
			} else {
				out.mirror(getMirrorCoords());
			}
		} else
			((ConicMirrorable) out).mirror(mirrorConic);

	}

	/**
	 * set inGeo to outGeo
	 */
	protected void setOutGeo() {
		if (mirror instanceof GeoConic && inGeo instanceof GeoLine) {
			((GeoLine) inGeo).toGeoConic((GeoConic) outGeo);
		}
		/*
		 * else if(mirror instanceof GeoConic && geoIn instanceof GeoConic &&
		 * geoOut instanceof GeoCurveCartesian){
		 * ((GeoConic)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut); }
		 */
		else if (mirror instanceof GeoConic && inGeo instanceof GeoConic
				&& outGeo instanceof GeoImplicitPoly) {
			((GeoConic) inGeo).toGeoImplicitPoly((GeoImplicitPoly) outGeo);
		} else if (inGeo instanceof GeoFunction && mirror != mirrorPoint) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else if (inGeo instanceof GeoPoly && mirror == mirrorConic) {
			((GeoPoly) inGeo).toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else
			outGeo.set(inGeo);
	}

	/**
	 * 
	 * @return inhom coords for mirror point
	 */
	protected Coords getMirrorCoords() {
		return mirrorPoint.getInhomCoords();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlain("AMirroredAtB", inGeo.getLabel(tpl),
				mirror.getLabel(tpl));

	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList))
			out = (Mirrorable) outGeo;

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoPoly) && mirror == mirrorConic)
			return new GeoCurveCartesian(cons);
		if ((geo instanceof GeoFunction) && mirror != mirrorPoint)
			return new GeoCurveCartesian(cons);
		if (geo.isLimitedPath() && mirror == mirrorConic)
			return new GeoConicPart(cons, GeoConicPart.CONIC_PART_ARC);
		if (mirror instanceof GeoConic && geo instanceof GeoLine) {
			return new GeoConic(cons);
		}
		if (mirror instanceof GeoConic
				&& geo instanceof GeoConic
				&& (!((GeoConic) geo).isCircle() || !((GeoConic) geo)
						.keepsType()))
			return new GeoImplicitPoly(cons);
		if (geo instanceof GeoPoly
				|| (geo.isLimitedPath() && mirror != mirrorConic))
			return copyInternal(cons, geo);
		if (geo.isGeoList())
			return new GeoList(cons);
		return copy(geo);
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (mirror != mirrorConic) {
			super.transformLimitedPath(a, b);
			return;
		}

		GeoConicPart arc = (GeoConicPart) b;
		arc.setParameters(0, 6.28, true);
		if (a instanceof GeoRay) {
			transformedPoint.removePath();
			setTransformedObject(((GeoRay) a).getStartPoint(), transformedPoint);
			compute();
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(arc.getPointParam(0.5), transformedPoint);
			compute();
			if (!((GeoRay) a).isOnPath(transformedPoint,
					Kernel.STANDARD_PRECISION))
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);

			setTransformedObject(a, b);
		} else if (a instanceof GeoSegment) {
			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(((GeoSegment) a).getStartPoint(),
					transformedPoint);
			compute();
			// if start point itself is on path, transformed point may have
			// wrong path param #2306
			transformedPoint.removePath();
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();

			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(((GeoSegment) a).getEndPoint(),
					transformedPoint);
			compute();

			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			if (arc.isOnPath(transformedPoint, Kernel.STANDARD_PRECISION))
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);
			setTransformedObject(a, b);
		}
		if (a instanceof GeoConicPart) {
			transformLimitedConic(a, b);
		}
	}

	@Override
	public boolean swapOrientation(GeoConicPart arc) {
		if (arc == null) {
			return true;
		} else if (mirror != mirrorConic) {
			return arc.positiveOrientation();
		}
		GeoVec2D arcCentre = arc.getTranslationVector();
		GeoVec2D mirrorCentre = mirrorConic.getTranslationVector();
		double dist = MyMath.length(arcCentre.getX() - mirrorCentre.getX(),
				arcCentre.getY() - mirrorCentre.getY());
		return !Kernel.isGreater(dist, arc.halfAxes[0]);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnMirror(geo, this, scope);
	}

	@Override
	public double getAreaScaleFactor() {
		return -1;
	}

}
