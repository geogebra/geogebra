/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Product of list elements
 * 
 * @author Michael Borcherds
 * @version 2008-07-27
 */

public class AlgoProduct extends AlgoStats1D {

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            numbers
	 * @param freq
	 *            frequencies
	 */
	public AlgoProduct(Construction cons, GeoList geoList,
			GeoList freq) {
		super(cons, geoList, freq, AlgoStats1D.STATS_PRODUCT);
	}

	@Override
	public Commands getClassName() {
		return Commands.Product;
	}
}
