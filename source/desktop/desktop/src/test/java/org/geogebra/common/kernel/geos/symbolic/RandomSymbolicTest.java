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

package org.geogebra.common.kernel.geos.symbolic;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.junit.Test;

public class RandomSymbolicTest extends BaseSymbolicTest {
	@Test
	public void testRandomOutputChanges() {
		List<String> changeable = List.of(
				"RandomBetween(1,1000000)",
				"RandomUniform(0,1)",
				"RandomNormal(0,1)",
				"RandomPoisson(10000000)",
				"RandomPolynomial(10,0,10)",
				"RandomBinomial(100000,0.5)",
				"RandomDiscrete(Sequence(500),Sequence(500)*0+1/500)",
				"Shuffle(Sequence(50))", "Sample(Sequence(50),30)"
		);
		changeable.forEach(this::testRandomizableCommand);
	}

	private void testRandomizableCommand(String command) {
		kernel.getConstruction().clearConstruction();
		GeoSymbolic s = add(command);
		String oldValue = s.toValueString(StringTemplate.maxDecimals);
		boolean changed = false;
		for (int retry = 0; retry < 3 && !changed; retry++) {
			s.getConstruction().updateConstruction(true);
			changed = !oldValue.equals(s.toValueString(StringTemplate.maxDecimals));
		}
		assertTrue("Value of " + command + " should have changed from " + oldValue,
				changed);
	}
}
