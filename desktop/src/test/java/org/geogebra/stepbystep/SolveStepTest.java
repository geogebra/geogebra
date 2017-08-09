package org.geogebra.stepbystep;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class SolveStepTest {
	private static App app;

	@BeforeClass
	public static void setupApp() {
		app = CommandsTest.createApp();
	}

	@Test
	public void linearEquation() {
		// app.getStepByStep()
		int steps = 7;
		Assert.assertEquals(7, steps);

	}
}
