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
 * Text value
 *
 */
public interface TextValue extends ExpressionValue {
	/**
	 * @return this text as MyStringBuffer
	 */
	public MyStringBuffer getText();

	/**
	 * @return string value of this
	 */
	public String getTextString();

}
