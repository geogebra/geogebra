/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus + ggb3D
 * @version
 */
public abstract class GeoVec extends GeoElement3D {

	public Coords v;

	private int m_length;

	public GeoVec(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}

	public GeoVec(Construction c, int n) {
		this(c);
		m_length = n;
		v = new Coords(n);
	}

	/** Creates new GeoVec with coordinates coords[] and label */
	public GeoVec(Construction c, double[] coords) {
		this(c, coords.length);
		setCoords(coords);
	}

	/** Copy constructor */
	/*
	 * public GeoVec(Construction c, GeoVec vec) { super(c); set(vec); }
	 */

	public void setCoords(Coords vals) {
		v.set(vals);

	}

	public void setCoords(double[] vals) {
		v.set(vals);
		// Application.debug("v="+v.toString());

	}

	public void setCoords(CoordMatrix v0) {
		setCoords(v0.get());
	}

	public void setCoords(GeoVec vec) {
		setCoords(vec.v);
	}

	public void setCoords(GeoPointND p) {
		setCoords(p.getCoordsInD(3));
	}

	final public Coords getCoords() {
		return v;
	}

	public void translate(Coords v0) {

		Coords v1 = v.add(v0).getColumn(1);
		setCoords(v1);
	}

}
