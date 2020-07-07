package org.geogebra.common.kernel.commands;

import java.util.List;

import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Test;

public class CommandFilterTest extends AlgebraTest {

	@Test
	public void noCASfilterTest() {
		CommandFilter cf = CommandFilterFactory
				.createNoCasCommandFilter();
		app.getKernel().getAlgebraProcessor().addCommandFilter(cf);
		for (Commands cmd0 : Commands.values()) {
			Commands cmd = cmd0;
			if (cmd0 == Commands.Derivative) {
				cmd = Commands.NDerivative;
			}
			if (cmd0 == Commands.Integral || cmd0 == Commands.IntegralBetween
					|| cmd0 == Commands.NIntegral
					|| cmd0 == Commands.Factors
					|| NoExceptionsTest.betaCommand(cmd0, app)
					|| AlgebraTestHelper.internalCAScommand(cmd0)) {
				continue;
			}
			if (cf.isCommandAllowed(cmd)) {
				List<Integer> signature = CommandSignatures
						.getSigneture(cmd.name());
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
}
