/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.main.AbstractApplication;

public class Kernel extends AbstractKernel {


	public Kernel(AbstractApplication app) {
		super(app);

	}

	public Kernel() {
		super();
	}

	
	

	
	/**
	 * Evaluates an expression in MathPiper syntax with.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 * 
	 *             final public String evaluateMathPiper(String exp) { if
	 *             (ggbCAS == null) { getGeoGebraCAS(); }
	 * 
	 *             return ggbCAS.evaluateMathPiper(exp); }
	 */

	/**
	 * Evaluates an expression in Maxima syntax with.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 * 
	 *             final public String evaluateMaxima(String exp) { if (ggbCAS
	 *             == null) { getGeoGebraCAS(); }
	 * 
	 *             return ggbCAS.evaluateMaxima(exp); }
	 */

	/**
	 * Returns this kernel's GeoGebraCAS object.
	 */



	// end G.Sturr

	

	/**
	 * returns 10^(-PrintDecimals)
	 * 
	 * final public double getPrintPrecision() { return PRINT_PRECISION; }
	 */

	/*
	 * GeoElement specific
	 */

	
	// final public void notifyRemoveAll(View view) {
	// Iterator it = cons.getGeoSetConstructionOrder().iterator();
	// while (it.hasNext()) {
	// GeoElement geo = (GeoElement) it.next();
	// view.remove(geo);
	// }
	// }

	/**
	 * Tells views to update all labeled elements of current construction.
	 * 
	 * final public static void notifyUpdateAll() {
	 * notifyUpdate(kernelConstruction.getAllGeoElements()); }
	 */

	

	
	
	
}
