package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.debug.Analytics;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraProcessorTests extends BaseUnitTest {

	private AlgebraProcessor processor;
	private ErrorHandler errorHandler;

	@Before
	public void setupTest() {
		errorHandler = Mockito.mock(ErrorHandler.class);
		processor = getAlgebraProcessor();
	}

	@Test
	public void testCopyingPlainVariables() {
		EvalInfo info = new EvalInfo(true).withCopyingPlainVariables(true);
		GeoElementND[] elements = evalCommand(
				"a=1",  info);
		Assert.assertNotNull(elements);
		Assert.assertEquals(elements.length, 1);
		GeoElementND a = elements[0];

		elements = evalCommand("a", info);
		Assert.assertNotNull(elements);
		Assert.assertEquals(elements.length, 1);
		GeoElementND b = elements[0];

		Assert.assertNotEquals(a, b);
		AlgoElement parentAlgo = b.getParentAlgorithm();
		Assert.assertNotNull(parentAlgo);
		assertThat(parentAlgo, is(instanceOf(AlgoDependentGeoCopy.class)));
	}

	@Test
	public void testFunctionLikeMultiplication() {
		GeoElement element = add("x(x + 2)");
		assertThat(element, CoreMatchers.<GeoElement>instanceOf(GeoFunction.class));
	}

	@Test
	public void testExceptionThrowing() {
		shouldFail("x y");
		shouldFail("xy");
		shouldFail("a");
		shouldFail("(1,1)");
		shouldFail("x");
		shouldFail("1+" + Unicode.IMAGINARY);
	}

	private void shouldFail(String string) {
		Throwable err = null;
		try {
			processor.convertToDouble(string);
		} catch (Throwable thrown) {
			err = thrown;
		}
		Assert.assertTrue(err instanceof NumberFormatException);
	}

	@Test
	public void testConversion() {
		shouldParseAs("-1", -1);
		shouldParseAs("-1,500", -1.5);
		shouldParseAs("360deg", 2 * Math.PI);
	}

	@Test
	public void onlyAlgebraInputShouldBeLogged() {
		ArrayList<Object> loggedCommands = new ArrayList<>();
		Analytics mockAnalytics = new Analytics() {

			@Override
			protected void recordEvent(String name, @Nullable Map<String, Object> params) {
				loggedCommands.add(Objects.requireNonNull(params).get(Analytics.Param.COMMAND));
			}
		};
		Analytics.setInstance(mockAnalytics);
		evalCommand("Midpoint((0,0),(2,2))", EvalInfoFactory.getEvalInfoForAV(getApp()));
		evalCommand("Line((0,0),(2,2))", EvalInfoFactory.getEvalInfoForRedefinition(getKernel(),
				new GeoLine(getConstruction()), true));
		evalCommand("Ray((0,0),(2,2))", new EvalInfo(true, false));
		assertThat(loggedCommands, is(Arrays.asList("Midpoint", "Line")));
	}

	@Test
	public void testOutputFilteringFiltersExpressions() {
		// Set up
		setErrorHandler(errorHandler);
		ExpressionFilter outputFilter = mock(ExpressionFilter.class);
		// Restrict all output
		when(outputFilter.isAllowed(any())).thenReturn(false);
		getAlgebraProcessor().addOutputExpressionFilter(outputFilter);

		// Assertions
		assertNull(add("1+1"));
		assertNull(add("(1,2)"));
		assertNull(add("x^2"));
		// Verify mocks were called exactly 3 times
		verify(outputFilter, times(3)).isAllowed(any());
		verify(errorHandler, times(3)).showError(any());
		// Assert that the geos are deleted from the construction
		assertEquals(0, getConstruction().steps());

		// Tear down
		resetErrorHandler();
		getAlgebraProcessor().removeOutputExpressionFilter(outputFilter);
	}

	private GeoElementND[] evalCommand(String s, EvalInfo info) {
		return processor.processAlgebraCommandNoExceptionHandling(s, false,
				errorHandler, info, null);
	}

	private void shouldParseAs(String string, double i) {
		Assert.assertEquals(processor.convertToDouble(string), i, DELTA);
	}
}
