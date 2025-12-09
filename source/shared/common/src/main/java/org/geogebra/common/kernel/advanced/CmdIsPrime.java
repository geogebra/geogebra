/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneNumber;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * IsPrime[number]
 * 
 * @author Zbynek Konecny
 *
 */
public class CmdIsPrime extends CmdOneNumber {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIsPrime(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num, String label) {
		AlgoIsPrime algo = new AlgoIsPrime(cons, label, num);
		return algo.getResult();
	}

}
