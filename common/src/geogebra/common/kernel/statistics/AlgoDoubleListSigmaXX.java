/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoList;

/**
 * Sum of squares of a list
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoDoubleListSigmaXX extends AlgoStats2D {

	public AlgoDoubleListSigmaXX(Construction cons, String label, GeoList geoListx, GeoList geoListy) {
        super(cons,label,geoListx,geoListy,AlgoStats2D.STATS_SIGMAXX);
    }

	@Override
	public Commands getClassName() {
		return Commands.SigmaXX;
	}
}
