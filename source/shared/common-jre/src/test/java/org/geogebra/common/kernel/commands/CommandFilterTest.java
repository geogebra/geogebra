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

package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.App;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Test;

public class CommandFilterTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void noCASfilterTest() {
		CommandFilter cf = CommandFilterFactory
				.createNoCasCommandFilter();
		App app = getApp();
		app.getKernel().getAlgebraProcessor().addCommandFilter(cf);
		for (Commands cmd0 : Commands.values()) {
			Commands cmd = cmd0;
			if (cmd0 == Commands.Derivative) {
				cmd = Commands.NDerivative;
			}
			if (cmd0 == Commands.Integral || cmd0 == Commands.IntegralBetween
					|| cmd0 == Commands.NIntegral
					|| cmd0 == Commands.Factors
					|| cmd0 == Commands.Polyhedron
					|| AlgebraTestHelper.internalCAScommand(cmd0)) {
				continue;
			}
			if (cf.isCommandAllowed(cmd)) {
				List<Integer> signature = CommandSignatures
						.getSignature(cmd.name(), app);
				if (signature != null && !signature.contains(0)) {
					AlgebraTestHelper.shouldFail(cmd + "()", "number of arg",
							"only",
							app);
				}
			} else {
				AlgebraTestHelper.shouldFail(cmd + "()", "Unknown command",
						"only", app);
			}

		}
	}

	@Test
	public void noCasCommandsInSuiteAndClassic() {
		List<String> integralsClassic = getApp().getCommandDictionary().getCompletions("Integ")
				.stream().map(c -> c.content).collect(Collectors.toList());
		// should not contain IntegralSymbolic
		assertEquals(Arrays.asList("Integral", "IntegralBetween",
				"IsInteger", "NIntegral"), integralsClassic);
	}
}
