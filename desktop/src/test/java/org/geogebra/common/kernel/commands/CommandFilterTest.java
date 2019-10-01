package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilterFactory;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Test;

public class CommandFilterTest extends AlgebraTest {

	@Test
	public void noCASfilterTest() {
		CommandNameFilter cf = CommandNameFilterFactory
				.createNoCasCommandNameFilter();
		app.getKernel().getAlgebraProcessor().addCommandNameFilter(cf);
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
                if (!AlgebraTestHelper.mayHaveZeroArgs(cmd.name())) {

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
