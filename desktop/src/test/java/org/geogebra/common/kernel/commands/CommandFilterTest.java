package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilterFactory;
import org.geogebra.common.main.App;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandFilterTest extends AlgebraTest {

	static App app;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
	}

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
					|| internalCAScommand(cmd0)) {
				continue;
			}
			if (cf.isCommandAllowed(cmd)) {
				if (!AlgebraTest.mayHaveZeroArgs(cmd.name())) {

					AlgebraTest.shouldFail(cmd + "()", "number of arg", "only",
							app);
				}
			} else {
				AlgebraTest.shouldFail(cmd + "()", "Unknown command",
						"only", app);
			}

		}
	}
}
