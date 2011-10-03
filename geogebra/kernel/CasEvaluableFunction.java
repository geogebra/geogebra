/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Interface to unify object types that allow evaluation with CAS commands,
 * like getting the derivative of a GeoFunction or GeoCurveCartesian.
 * @author Markus Hohenwarter
 */
public interface CasEvaluableFunction {
	
	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd the GeoGebraCAS command needs to include % in all places
	 * where the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f the function that the CAS command is applied to
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f, boolean symbolic);
		
	public String toSymbolicString();
	public String getVarString();
	public GeoElement toGeoElement();
}
