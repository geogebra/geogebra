/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoLimitBelow extends AlgoLimit {
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param f function
	 * @param num number
	 */
	public AlgoLimitBelow(Construction cons, String label, GeoFunction f,
			NumberValue num) {
		super(cons, label, f, num);
	}

	@Override
	public Commands getClassName() {
		return Commands.LimitBelow;
	}

	@Override
	protected int getDirection(){
		return 1;
	}

}
