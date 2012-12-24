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
import geogebra.common.kernel.algos.AlgoStats1D;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoList;

/**
 * Sxx of a list
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoSXX extends AlgoStats1D {

	/**
	 * @param cons construction
	 * @param label label
	 * @param geoList list to process
	 */
	public AlgoSXX(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SXX);
    }

    @Override
	public Commands getClassName() {
        return Commands.SXX;
    }
}
