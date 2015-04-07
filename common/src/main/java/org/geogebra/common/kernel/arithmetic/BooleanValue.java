/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

/**
 * Boolean values
 * 
 * @author Markus
 */
public interface BooleanValue extends ExpressionValue {
	/** @return this boolean as MyBoolean */
	public MyBoolean getMyBoolean();

	/** @return boolean value */
	public boolean getBoolean();

	/** @return double value (1 for true, 0 for false) */
	public double getDouble();
}
