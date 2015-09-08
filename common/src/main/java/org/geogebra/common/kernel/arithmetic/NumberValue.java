/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.ToGeoElement;

/**
 * Interface for elements with numeric value (numerics, segments, polygons, ...)
 * and their counterparts from geogebra.common.kernel.arithmetic (MyDouble)
 * 
 * @author Markus
 */
public interface NumberValue extends ExpressionValue, ToGeoElement {
	/**
	 * @return MyDouble whose value equals #getDouble()
	 */
	public MyDouble getNumber();

	/**
	 * 
	 * @return true for angles
	 */
	public int getAngleDim();

	/**
	 * 
	 * @return value of this number
	 */
	public double getDouble();

	/**
	 * @return whether this value is defined or not
	 */
	public boolean isDefined();

	public String getLabel(StringTemplate tpl);
}
