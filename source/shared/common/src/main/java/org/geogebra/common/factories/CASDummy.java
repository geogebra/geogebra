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

package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.CASGenericInterface;

public class CASDummy extends CASgiac implements CASGenericInterface {

	public CASDummy(CASparser casParser) {
		super(casParser);
	}

	@Override
	public void clearResult() {
		//
	}

	@Override
	public boolean externalCAS() {
		return false;
	}

	@Override
	public String evaluateCAS(String exp) {
		return "?";
	}

	@Override
	protected String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable {
		return "?";
	}

}
