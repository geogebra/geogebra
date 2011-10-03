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
 * Sxx of a list
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoDoubleListSXX extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoDoubleListSXX(Construction cons, String label, GeoList geoListx, GeoList geoListy) {
        super(cons,label,geoListx,geoListy,AlgoStats2D.STATS_SXX);
    }

    public String getClassName() {
        return "AlgoDoubleListSXX";
    }
}
