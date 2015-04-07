/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLineVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDiameterVector extends AlgoDiameterVectorND {

	/** Creates new AlgoDiameterVector */
	public AlgoDiameterVector(Construction cons, String label, GeoConicND c,
			GeoVectorND v) {
		super(cons, label, c, v);
	}

	@Override
	protected void createOutput(Construction cons) {
		diameter = new GeoLine(cons);
	}

	// calc diameter line of v relativ to c
	@Override
	public final void compute() {
		c.diameterLine((GeoVector) v, (GeoLine) diameter);
	}

}
