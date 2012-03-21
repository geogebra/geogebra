/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.arithmetic;

/**
 * Interface for all expression values that allow replacing
 * a sub-value in their definition tree
 */
public interface ReplaceableValue extends ExpressionValue {

	/**
	 * Replaces every oldOb by newOb in this replaceable object.
	 * @param oldOb old value
	 * @param newOb new value
	 * @return resulting expression
	 */
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb);
	
	/**
	 * @param var dummy variable name
	 * @param newOb new object
	 * @return resulting expression
	 */
	public boolean replaceGeoDummyVariables(String var, ExpressionValue newOb);
	
}
