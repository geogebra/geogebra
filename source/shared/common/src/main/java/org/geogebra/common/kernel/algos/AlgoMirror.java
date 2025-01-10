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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.ConicMirrorable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.MirrorAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoMirror extends AlgoTransformation implements
		SymbolicParametersBotanaAlgo {

	protected Mirrorable out;
	private GeoLineND mirrorLine;
	protected GeoPointND mirrorPoint;
	private GeoConic mirrorConic;
	protected GeoElement mirror;

	private GeoPoint transformedPoint;
	private MirrorAdapter mirrorBotana;

	/**
	 * Creates new "mirror at point" algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param in
	 *            source geo
	 * @param p
	 *            mirror point
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
	 *            construction
	 * @param in
	 *            source geo
	 * @param p
	 *            mirror point
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
	 *            construction
	 * @param label
	 *            output label
	 * @param in
	 *            source geo
	 * @param c
	 *            mirror conic
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoConic c) {

		this(cons, in, c);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at conic" algo
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            source geo
	 * @param c
	 *            mirror conic
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
	 *            construction
	 * @param label
	 *            output label
	 * @param in
	 *            source geo
	 * @param g
	 *            mirror line
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoLineND g) {

		this(cons, in, g);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at line" algo
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            source geo
	 * @param g
	 *            mirror line
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
	 * @param cons1
	 *            construction
	 * @param in
	 *            transformed geo
	 * @param setMirror
	 *            mirror
	 */
	public void endOfConstruction(Construction cons1, GeoElement in,
			GeoElement setMirror) {

		this.mirror = setMirror;

		inGeo = in;
		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Mirrorable) {
			out = (Mirrorable) outGeo;
		}
		setInputOutput();

		transformedPoint = new GeoPoint(cons1);
		compute();
		if (inGeo.isGeoFunction()) {
			cons1.registerEuclidianViewCE(this);
		}
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

		setOnlyOutput(outGeo);
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
			outGeo.setInverseFill(
					((Region) inGeo).isInRegion(v.getX(), v.getY())
							^ inGeo.isInverseFill());
		}

		computeRegardingMirror();

		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
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
			if (outGeo instanceof GeoFunction) {
				((GeoFunction) outGeo).mirror(getMirrorCoords());
			} else {
				out.mirror(getMirrorCoords());
			}
		} else {
			if (out instanceof ConicMirrorable) {
				((ConicMirrorable) out).mirror(mirrorConic);
			}
		}

	}

	/**
	 * set inGeo to outGeo
	 */
	@Override
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
				&& outGeo instanceof GeoImplicit) {
			((GeoConic) inGeo).toGeoImplicitCurve((GeoImplicit) outGeo);
		} else if (inGeo instanceof GeoFunction && mirror != mirrorPoint) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else if (inGeo instanceof GeoPoly && mirror == mirrorConic) {
			((GeoPoly) inGeo).toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else {
			super.setOutGeo();
		}
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
		return getLoc().getPlainDefault("AMirroredAtB", "%0 Mirrored at %1",
				inGeo.getLabel(tpl),
				mirror.getLabel(tpl));

	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList) && (outGeo instanceof Mirrorable)) {
			out = (Mirrorable) outGeo;
		}

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoPoly) && mirror == mirrorConic) {
			return new GeoCurveCartesian(cons);
		}
		if ((geo instanceof GeoFunction) && mirror != mirrorPoint) {
			return new GeoCurveCartesian(cons);
		}
		if (geo.isLimitedPath() && mirror == mirrorConic) {
			return new GeoConicPart(cons, GeoConicNDConstants.CONIC_PART_ARC);
		}
		if (mirror instanceof GeoConic && geo instanceof GeoLine) {
			return new GeoConic(cons);
		}
		if (mirror instanceof GeoConic && geo instanceof GeoConic
				&& (!((GeoConic) geo).isCircle()
						|| !((GeoConic) geo).keepsType())) {
			return kernel.newImplicitPoly(cons).toGeoElement();
		}
		if (geo instanceof GeoPoly
				|| (geo.isLimitedPath() && mirror != mirrorConic)) {
			return copyInternal(cons, geo);
		}
		if (geo.isGeoList()) {
			return new GeoList(cons);
		}
		return copy(geo);
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (mirror != mirrorConic) {
			super.transformLimitedPath(a, b);
			return;
		}

		GeoConicPart arc = (GeoConicPart) b;
		arc.setParameters(0, Kernel.PI_2, true);
		transformedPoint.removePath();
		if (a instanceof GeoRay) {
			transformedPoint.setCoords(((GeoRay) a).getStartPoint());
			transformedPoint.mirror(mirrorConic);
			double d = getTransformedParam(arc);

			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			double e = getTransformedParam(arc);
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);

			transformedPoint.setCoords(arc.getPointParam(0.5));
			transformedPoint.mirror(mirrorConic);
			transformedPoint.removePath();
			if (!((GeoRay) a).isOnPath(transformedPoint,
					Kernel.STANDARD_PRECISION)) {
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);
			}
		} else if (a instanceof GeoSegment) {
			transformedPoint.setCoords(((GeoSegment) a).getStartPoint());
			transformedPoint.mirror(mirrorConic);
			if (arc.getType() == GeoConicNDConstants.CONIC_LINE) {
				arc.getLines()[0].setStartPoint(transformedPoint.copy());
			}
			double d = getTransformedParam(arc);

			transformedPoint.setCoords(((GeoSegment) a).getEndPoint());
			transformedPoint.mirror(mirrorConic);
			if (arc.getType() == GeoConicNDConstants.CONIC_LINE) {
				arc.getLines()[0].setEndPoint(transformedPoint.copy());
			}
			double e = getTransformedParam(arc);
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			if (arc.isOnPath(transformedPoint, Kernel.STANDARD_PRECISION)) {
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);
			}
		}
		if (a instanceof GeoConicPart) {
			transformLimitedConic(a, b);
		}
	}

	private double getTransformedParam(GeoConicPart arc) {
		Coords coords = transformedPoint.getCoordsInD2(arc.getCoordSys());
		PathParameter pp = new PathParameter();
		arc.pointChanged(coords, pp);
		return pp.getT();
	}

	@Override
	public boolean swapOrientation(GeoConicPartND arc) {
		if (arc == null) {
			return true;
		} else if (mirror != mirrorConic || !(arc instanceof GeoConicPart)) {
			return arc.positiveOrientation();
		}
		GeoVec2D arcCentre = ((GeoConicPart) arc).getTranslationVector();
		GeoVec2D mirrorCentre = mirrorConic.getTranslationVector();
		double dist = MyMath.length(arcCentre.getX() - mirrorCentre.getX(),
				arcCentre.getY() - mirrorCentre.getY());
		return !DoubleUtil.isGreater(dist, ((GeoConicPart) arc).halfAxes[0]);
	}

	@Override
	public double getAreaScaleFactor() {
		return -1;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (mirrorBotana == null) {
			mirrorBotana = new MirrorAdapter();
		}
		return mirrorBotana.getBotanaVars();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (mirrorBotana == null) {
			mirrorBotana = new MirrorAdapter();
		}
		return this.mirrorBotana.getBotanaPolynomials(geo, inGeo, mirrorLine,
				mirrorPoint, mirrorConic);
	}

}
