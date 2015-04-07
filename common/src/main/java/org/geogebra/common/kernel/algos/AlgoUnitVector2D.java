/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoUnitVector2D extends AlgoUnitVector {

	protected double x, y;

	/** Creates new AlgoOrthoVectorVector */
	public AlgoUnitVector2D(Construction cons, String label, GeoElement inputGeo) {
		super(cons, label, inputGeo);
	}

	@Override
	final protected GeoVectorND createVector(Construction cons) {
		GeoVector ret = new GeoVector(cons);
		ret.z = 0.0d;
		return ret;
	}

	// line through P normal to v
	@Override
	final public void compute() {
		setXY();
		length = MyMath.length(x, y);
		((GeoVector) u).x = x / length;
		((GeoVector) u).y = y / length;
	}

	/**
	 * 
	 * set x, y to compute vector
	 */
	abstract protected void setXY();

}
