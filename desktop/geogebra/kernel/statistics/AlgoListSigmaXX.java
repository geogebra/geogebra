/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;

/**
 * Sum of squares of x-coords of a list of Points
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoListSigmaXX extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoListSigmaXX(Construction cons, String label, GeoList geoListx) {
        super(cons,label,geoListx,AlgoStats2D.STATS_SIGMAXX);
    }

    public String getClassName() {
        return "AlgoListSigmaXX";
    }
}
