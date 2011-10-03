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
 * Standard deviation of y-coords of a list of Points
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoListSDY extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoListSDY(Construction cons, String label, GeoList geoListy) {
        super(cons,label,geoListy,AlgoStats2D.STATS_SDY);
    }

    public String getClassName() {
        return "AlgoListSDY";
    }
}

