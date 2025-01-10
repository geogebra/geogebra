/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * VectorValue.java
 *
 * Created on 03. October 2001, 10:09
 */

package org.geogebra.common.kernel.arithmetic3D;

import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;

/**
 *
 * @author Markus + ggb3D
 */
public interface Vector3DValue extends VectorNDValue {
	/**
	 * Converts vector to array of coords
	 * 
	 * @return array of coords
	 */
	@Override
	public double[] getPointAsDouble();

	@Override
	public Geo3DVecInterface getVector();
}
