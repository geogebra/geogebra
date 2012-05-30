/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAttachCopyToView.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolyLineInterface;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.MyMath;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoAttachCopyToView extends AlgoTransformation {

	private MatrixTransformable out;
	private GeoElement inGeo, outGeo;
	private NumberValue viewID;
	private GeoPointND corner1, corner3, corner5;

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param matrix
	 */
	public AlgoAttachCopyToView(Construction cons, String label, GeoElement in,
			NumberValue viewID, GeoPointND corner1, GeoPointND corner3,GeoPointND corner5) {
		this(cons,in,viewID,corner1,corner3,corner5);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 * @param in
	 * @param matrix
	 */
	public AlgoAttachCopyToView(Construction cons,  GeoElement in,
			NumberValue viewID, GeoPointND corner1, GeoPointND corner3,GeoPointND corner5) {
		super(cons);

		this.viewID = viewID;
		this.corner1 = corner1;
		this.corner3 = corner3;
		this.corner5 = corner5;
		
		inGeo = in.toGeoElement();
		if ((inGeo instanceof GeoPolyLineInterface) || inGeo.isLimitedPath()) {
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
		cons.registerEuclidianViewCE(this);
		
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoAttachCopyToView;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[5];
		input[0] = inGeo;
		input[1] = viewID.toGeoElement();
		input[2] = corner1.toGeoElement();
		input[3] = corner3.toGeoElement();
		input[4] = corner5.toGeoElement();

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
		int view = (int) viewID.getDouble();
		AbstractEuclidianView ev = null;
		if (view == 2)
			ev = app.getEuclidianView2();
		else if(view==1)
			ev = app.getEuclidianView1();
		if(ev==null && view!=0){
			outGeo.setUndefined();
			return;
		}
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo.isGeoFunction()) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else {
			outGeo.set(inGeo);
		}
		if(view==0)
			return;
		
			
		
		
		GeoPoint2 c1 = (GeoPoint2) corner1;
		GeoPoint2 c3 = (GeoPoint2) corner3;
		GeoPoint2 c5 = (GeoPoint2) corner5;
		
	double[][] m =	MyMath.adjoint(c1.getX()/c1.getZ(), c3.getX()/c3.getZ(), c1.getX()/c1.getZ(), 
				c1.getY()/c1.getZ(), c3.getY()/c3.getZ(), c3.getY()/c3.getZ(), 
				1, 1, 1);
	out.matrixTransform(m[0][0], m[1][0], m[2][0], m[0][1], m[1][1], m[2][1], m[0][2], m[1][2], m[2][2]);
	
	double x1=ev.getXmin(),x2=ev.getXmin()+(ev.getXmax()-ev.getXmin())*c5.getX()/c5.getZ()/ev.getWidth(),
			y2=ev.getYmax(),y1=ev.getYmax()-(ev.getYmax()-ev.getYmin())*c5.getY()/c5.getZ()/ev.getHeight();
			out.matrixTransform(x1, x2, x1, y1, y2, y2, 1, 1, 1);
		//TODO find out why this is needed	
		outGeo.updateCascade();
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
	public boolean swapOrientation(boolean posOrientation) {
		return false;
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart)) {
			super.transformLimitedPath(a, b);
		} else {
			super.transformLimitedConic(a, b);
		}

	}

}
