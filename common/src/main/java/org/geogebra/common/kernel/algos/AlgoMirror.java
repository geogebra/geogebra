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
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
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
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoMirror extends AlgoTransformation implements
		RestrictionAlgoForLocusEquation, SymbolicParametersBotanaAlgo {

	protected Mirrorable out;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	private GeoLineND mirrorLine;
	protected GeoPointND mirrorPoint;
	private GeoConic mirrorConic;
	protected GeoElement mirror;

	private GeoPoint transformedPoint;

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

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

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_LINE) {

			GeoPoint P = (GeoPoint) inGeo;
			GeoLine l = (GeoLine) mirrorLine;

			if (P != null && l != null) {
				Variable[] vP = P.getBotanaVars(P);
				Variable[] vL = l.getBotanaVars(l);

				if (botanaVars == null) {
					botanaVars = new Variable[6];
					// C'
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// V
					botanaVars[2] = new Variable();
					botanaVars[3] = new Variable();
					// N
					botanaVars[4] = new Variable();
					botanaVars[5] = new Variable();
				}

				botanaPolynomials = new Polynomial[6];

				Polynomial v1 = new Polynomial(botanaVars[2]);
				Polynomial v2 = new Polynomial(botanaVars[3]);
				Polynomial c1 = new Polynomial(vP[0]);
				Polynomial c2 = new Polynomial(vP[1]);
				Polynomial c_1 = new Polynomial(botanaVars[0]);
				Polynomial c_2 = new Polynomial(botanaVars[1]);

				// CV = VC'
				botanaPolynomials[0] = v1.multiply(new Polynomial(2))
						.subtract(c_1).subtract(c1);
				botanaPolynomials[1] = v2.multiply(new Polynomial(2))
						.subtract(c_2).subtract(c2);

				//
				Variable[] A = new Variable[2];
				A[0] = vL[0];
				A[1] = vL[1];
				Variable[] B = new Variable[2];
				B[0] = vL[2];
				B[1] = vL[3];

				// A,V,B collinear
				botanaPolynomials[2] = Polynomial.collinear(A[0], A[1], B[0],
						B[1], botanaVars[2], botanaVars[3]);

				Polynomial a1 = new Polynomial(A[0]);
				Polynomial a2 = new Polynomial(A[1]);
				Polynomial b1 = new Polynomial(B[0]);
				Polynomial b2 = new Polynomial(B[1]);
				Polynomial n1 = new Polynomial(botanaVars[4]);
				Polynomial n2 = new Polynomial(botanaVars[5]);

				// CV orthogonal AB
				botanaPolynomials[3] = b1.subtract(a1).add(c2).subtract(n2);
				botanaPolynomials[4] = c1.subtract(b2).add(a2).subtract(n1);

				// C',N,V collinear
				botanaPolynomials[5] = Polynomial.collinear(botanaVars[0],
						botanaVars[1], botanaVars[2], botanaVars[3],
						botanaVars[4], botanaVars[5]);

				return botanaPolynomials;

			}throw new NoSymbolicParametersException();
			
		} else if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_POINT) {
			GeoPoint P1 = (GeoPoint) inGeo;
			GeoPoint P2 = (GeoPoint) mirrorPoint;

			if (P1 != null && P2 != null) {
				Variable[] vP1 = P1.getBotanaVars(P1);
				Variable[] vP2 = P2.getBotanaVars(P2);

				if (botanaVars == null) {
					botanaVars = new Variable[2];
					// P1'
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
				}

				botanaPolynomials = new Polynomial[2];
				
				Polynomial a1 = new Polynomial(vP1[0]);
				Polynomial a2 = new Polynomial(vP1[1]);
				Polynomial b1 = new Polynomial(vP2[0]);
				Polynomial b2 = new Polynomial(vP2[1]);
				Polynomial a_1 = new Polynomial(botanaVars[0]);
				Polynomial a_2 = new Polynomial(botanaVars[1]);

				// AB = BA'
				botanaPolynomials[0] = b1.multiply(new Polynomial(2))
						.subtract(a1).subtract(a_1);
				botanaPolynomials[1] = b2.multiply(new Polynomial(2))
						.subtract(a2).subtract(a_2);

				return botanaPolynomials;
			}
			throw new NoSymbolicParametersException();

		} else if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_CIRCLE) {

			GeoPoint P = (GeoPoint) inGeo;
			GeoConic c = (GeoConic) mirror;

			if (P != null && c != null) {
				Variable[] vP = P.getBotanaVars(P);
				Variable[] vc = c.getBotanaVars(c);

				if (botanaVars == null) {
					botanaVars = new Variable[8];
					// B'
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// B
					botanaVars[2] = vP[0];
					botanaVars[3] = vP[1];
					// O
					botanaVars[4] = vc[0];
					botanaVars[5] = vc[1];
					// A
					botanaVars[6] = vc[2];
					botanaVars[7] = vc[3];
				}

				botanaPolynomials = new Polynomial[2];

				Polynomial o1 = new Polynomial(vc[0]);
				Polynomial o2 = new Polynomial(vc[1]);
				Polynomial a1 = new Polynomial(vc[2]);
				Polynomial a2 = new Polynomial(vc[3]);
				Polynomial b1 = new Polynomial(vP[0]);
				Polynomial b2 = new Polynomial(vP[1]);
				Polynomial b_1 = new Polynomial(botanaVars[0]);
				Polynomial b_2 = new Polynomial(botanaVars[1]);

				// r^2
				Polynomial oa = (a1.subtract(o1)).multiply(a1.subtract(o1))
						.add((a2.subtract(o2)).multiply(a2.subtract(o2)));
				// (x-x_0)^2 + (y-y_0)^2
				Polynomial denominator = (b1.subtract(o1)).multiply(
						b1.subtract(o1)).add(
						(b2.subtract(o2)).multiply(b2.subtract(o2)));

				// formula for the coordinates of inverse point
				// from: http://mathworld.wolfram.com/Inversion.html
				botanaPolynomials[0] = oa.multiply(b1.subtract(o1)).add(
						(o1.subtract(b_1)).multiply(denominator));

				botanaPolynomials[1] = oa.multiply(b2.subtract(o2)).add(
						(o2.subtract(b_2)).multiply(denominator));

				return botanaPolynomials;

			}
			throw new NoSymbolicParametersException();

		} else {
			throw new NoSymbolicParametersException();
		}
	}
}
