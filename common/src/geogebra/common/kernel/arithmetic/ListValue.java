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

package geogebra.common.kernel.arithmetic;




/**
 *
 * @author  Markus
 */
public interface ListValue extends ExpressionValue {
	
	/**
	 * Returns a MyList object. 
	 * @return MyList representation of this list
	 */
	public MyList getMyList();
	
	/**
	 * Tries to return this list as an array of double values
	 * @return array of double values from this list
	 */
	public double[] toDouble();
	
	/**
	 * @return number of elements of this list
	 */
	public int size();
	
	/**
	 * @param i list index
	 * @return i-th element
	 */
	public ExpressionValue getListElement(int i);
		
}

