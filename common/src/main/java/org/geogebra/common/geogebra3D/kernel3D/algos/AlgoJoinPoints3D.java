/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoordSys1D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 *
 * @author ggb3D
 * 
 *          Joins two GeoPoint3Ds in a GeoSegment3D, GeoLine3D, ... regarding to
 *          geoClassType
 * 
 */
public class AlgoJoinPoints3D extends AlgoElement3D implements
		AlgoJoinPointsSegmentInterface {

	// inputs
	/** first point */
	private GeoPointND P;
	/** second point */
	private GeoPointND Q;
	/** polygon or polyhedron (when segment is part of) */
	private GeoElement poly;

	// output
	/** 1D coord sys */
	protected GeoCoordSys1D cs;
	/** the output is a segment, a line, ... */
	protected GeoClass geoClassType;

	/**
	 * Creates new AlgoJoinPoints3D
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of the segment/line/...
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @param geoClassType
	 *            type (GeoSegment3D, GeoLine3D, ...)
	 */
	public AlgoJoinPoints3D(Construction cons, String label, GeoPointND P,
			GeoPointND Q, GeoClass geoClassType) {
		this(cons, label, P, Q, null, geoClassType);
	}

	/**
	 * Creates new AlgoJoinPoints3D
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of the segment/line/...
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @param poly
	 *            poly polygon or polyhedron (when segment is part of)
	 * @param geoClassType
	 *            type (GeoSegment3D, GeoLine3D, ...)
	 */
	AlgoJoinPoints3D(Construction cons, String label, GeoPointND P,
			GeoPointND Q, GeoElement poly, GeoClass geoClassType) {
		this(cons, P, Q, poly, geoClassType);
		cs.setLabel(label);
	}

	/**
	 * Creates new AlgoJoinPoints3D
	 * 
	 * @param cons
	 *            the construction
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @param poly
	 *            polygon or polyhedron (when segment is part of)
	 * @param geoClassType
	 *            type (GeoSegment3D, GeoLine3D, ...)
	 */
	public AlgoJoinPoints3D(Construction cons, GeoPointND P, GeoPointND Q,
			GeoElement poly, GeoClass geoClassType) {
		super(cons);

		if (poly != null)
			setUpdateAfterAlgo(poly.getParentAlgorithm());

		this.P = P;
		this.Q = Q;
		this.poly = poly;
		this.geoClassType = geoClassType;

		switch (geoClassType) {
		case SEGMENT3D:
			cs = new GeoSegment3D(cons, P, Q);
			if (poly != null) {
				((GeoSegment3D) cs).setFromMeta(poly);
			}
			break;
		case LINE3D:
			cs = new GeoLine3D(cons, P, Q);
			break;
		case RAY3D:
			cs = new GeoRay3D(cons, P, Q);
			break;
		default:
			cs = null;
		}

		setInputOutput();

		compute();
	}

	@Override
	protected void setInputOutput() {
		if (poly == null) {
			setInputOutput(new GeoElement[] { (GeoElement) P, (GeoElement) Q },
					new GeoElement[] { cs });
		} else {
			setInputOutput(new GeoElement[] { (GeoElement) P, (GeoElement) Q,
					poly },
					new GeoElement[] { (GeoElement) P, (GeoElement) Q },
					new GeoElement[] { cs });
		}
	}

	/**
	 * return the first point
	 * 
	 * @return the first point
	 */
	public GeoPointND getP() {
		return P;
	}

	/**
	 * return the second point
	 * 
	 * @return the second point
	 */
	public GeoPointND getQ() {
		return Q;
	}

	/**
	 * return the 1D coord sys
	 * 
	 * @return the 1D coord sys
	 */
	public GeoCoordSys1D getCS() {
		return cs;
	}

	@Override
	public void compute() {

		if (poly != null) {
			if (!poly.isDefined())
				cs.setUndefined();
		}

		if ((((GeoElement) P).isDefined() || P.isInfinite())
				&& (((GeoElement) Q).isDefined() || Q.isInfinite())) {
			cs.setCoord(P, Q);
		} else {
			cs.setUndefined();
		}
	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		// if segment is part of a polygon, remove it
		if (poly != null) {
			poly.remove();
		}
	}

	public void modifyInputPoints(GeoPointND A, GeoPointND B) {

		// same points : return
		if ((P == A && Q == B) || (Q == A && P == B))
			return;

		for (int i = 0; i < input.length; i++)
			input[i].removeAlgorithm(this);

		P = A;
		Q = B;
		cs.setCoord(P, Q);
		setInputOutput();

		compute();
	}

	/**
	 * modify input polygon/polyhedron and points
	 * 
	 * @param p
	 *            polygon/polyhedron
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 */
	public void modifyInputPolyAndPoints(GeoElement p, GeoPointND A,
			GeoPointND B) {

		// same points : return
		if (p == poly && (P == A && Q == B) || (Q == A && P == B))
			return;

		for (int i = 0; i < input.length; i++)
			input[i].removeAlgorithm(this);

		poly = p;
		P = A;
		Q = B;
		cs.setCoord(P, Q);
		setInputOutput();

		compute();
	}

	@Override
	public Commands getClassName() {
		switch (geoClassType) {
		case SEGMENT3D:
			return Commands.Segment;
		case LINE3D:
			return Commands.Line;
		case RAY3D:
			return Commands.Ray;
		}
		return null;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		switch (geoClassType) {
		case SEGMENT3D:
			if (poly != null)
				sb.append(getLoc().getPlain("SegmentABofC", P.getLabel(tpl),
						Q.getLabel(tpl), poly.getNameDescription()));
			else
				sb.append(getLoc().getPlain("SegmentAB",
						((GeoElement) P).getLabel(tpl),
						((GeoElement) Q).getLabel(tpl)));
			break;
		case LINE3D:
			sb.append(getLoc().getPlain("LineThroughAB",
					((GeoElement) P).getLabel(tpl),
					((GeoElement) Q).getLabel(tpl)));
			break;
		case RAY3D:
			sb.append(getLoc().getPlain("RayThroughAB",
					((GeoElement) P).getLabel(tpl),
					((GeoElement) Q).getLabel(tpl)));
			break;
		}

		return sb.toString();
	}

	/**
	 * 
	 * @return parent polygon/polyhedron (if exists)
	 */
	public GeoElement getPoly() {
		return poly;
	}

	

}
