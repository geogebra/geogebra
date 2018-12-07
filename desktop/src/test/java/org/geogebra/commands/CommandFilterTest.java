package org.geogebra.commands;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandSelector;
import org.geogebra.common.kernel.commands.selector.NoCASCommandSelectorFactory;
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
		CommandSelector cs = new NoCASCommandSelectorFactory()
				.createCommandSelector();
		AlgebraTest.enableCAS(app, false);
		for (Commands cmd0 : Commands.values()) {
			Commands cmd = cmd0;
			if (cmd0 == Commands.Derivative) {
				cmd = Commands.NDerivative;
			}
			if (cmd0 == Commands.Integral || cmd0 == Commands.IntegralBetween
					|| cmd0 == Commands.NIntegral
					|| cmd0 == Commands.Factors
					|| NoExceptionsTest.betaCommand(cmd0)
					|| internalCAScommand(cmd0)) {
				continue;
			}
			if (cs.isCommandAllowed(cmd)) {
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
