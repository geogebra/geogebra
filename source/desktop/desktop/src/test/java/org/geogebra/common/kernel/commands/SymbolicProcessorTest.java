package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.desktop.headless.AppDNoGui;
import org.hamcrest.core.StringStartsWith;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class SymbolicProcessorTest {

	private static SymbolicProcessor processor;
	private static Kernel kernel;

	/** Set up the app */
	@BeforeClass
	public static void setUp() {
		AppDNoGui app = AlgebraTest.createApp();
		kernel = app.getKernel();
		processor = new SymbolicProcessor(kernel);
	}

	@Test
	public void symbolicExpressionTest() {
		Variable a = new Variable(kernel, "a");
		GeoElement aPlusA = processor.evalSymbolicNoLabel(a.wrap().plus(a));
		assertEquals("2 * a",
				aPlusA.toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void symbolicCommandTest() {
		Variable a = new Variable(kernel, "a");
		Command integral = new Command(kernel, "Integral", false);
		integral.addArgument(a.wrap().multiply(a));
		GeoElement integralASquared = processor.evalSymbolicNoLabel(integral);
		// the arbitrary constant index may change, use regexp
		assertThat(
				integralASquared.toValueString(StringTemplate.testTemplate),
				StringStartsWith.startsWith("1 / 3 * a^(3) + c_"));
	}

	@Test
	public void testFailedNestedCommandReevaluatesInContext() {
		SymbolicProcessor spy = Mockito.spy(processor);

		// Internal command should return ? from Giac
		MyDouble parameter = new MyDouble(kernel, Double.NaN);
		Command nestedCommand = new Command(kernel, "Integral", false);
		nestedCommand.addArgument(parameter.wrap());

		// Nested command, that carries some context,
		// That might help in evaluating nested command
		Command outerContext = new Command(kernel, "Solve", false);
		outerContext.addArgument(nestedCommand.wrap());

		spy.evalSymbolicNoLabel(outerContext.wrap());

		// Assert that no calls to this method contains evaluated subexpressio
		Mockito.verify(spy, Mockito.times(2)).doEvalSymbolicNoLabel(argThat(argument ->
				!argument.inspect(v -> v instanceof GeoSymbolic)), any());
	}
}
