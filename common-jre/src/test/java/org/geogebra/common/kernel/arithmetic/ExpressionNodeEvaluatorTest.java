package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ScientificOperationArgumentFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.TestErrorHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class ExpressionNodeEvaluatorTest extends BaseUnitTest {

	@Test
	public void testOperationFilterPassing() {
		ExpressionFilter filter = Mockito.mock(ExpressionFilter.class);
		Mockito.when(filter.isAllowed(Mockito.any(ValidExpression.class))).thenReturn(true);
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		GeoElementND[] minusValue = process(algebraProcessor, "1-2");
		Assert.assertNotNull(minusValue);
	}

	@Test(expected = MyError.class)
	public void testOperationFilterRejecting() {
		ExpressionFilter filter = Mockito.mock(ExpressionFilter.class);
		Mockito.doReturn(false).when(filter).isAllowed(
				Mockito.argThat(ev -> ev.isOperation(Operation.PLUS)));
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		process(algebraProcessor, "1+2");
	}

	private AlgebraProcessor createAlgebraProcessor(ExpressionFilter filter) {
		AlgebraProcessor ap = new AlgebraProcessor(getKernel(), new CommandDispatcherJre(getKernel()));
		ap.addInputExpressionFilter(filter);
		return ap;
	}

	private ExpressionNode parseExpression(String expression) {
		try {
			return getParser().parseExpression(expression);
		} catch (Exception exception) {
			Assert.fail("Should not throw an exception");
		}
		return null;
	}

	private Parser getParser() {
		return getKernel().getParser();
	}


	@Test(expected = MyError.class)
	public void testNoListOperationsInScientific() {
		ExpressionFilter filter = ScientificOperationArgumentFilter.INSTANCE;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		process(algebraProcessor, "{1,2,3} + 3");
	}

	@Test
	public void testListArgumentsInScientific() {
		ExpressionFilter filter = ScientificOperationArgumentFilter.INSTANCE;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		GeoElementND[] values =	process(algebraProcessor, "mean({1,2,3}, {4,5,6})");
		assertThat(values[0].evaluateDouble(), is(2.1333333333333333));
	}

	private GeoElementND[] process(AlgebraProcessor ap, String input) {
		return ap.processAlgebraCommandNoExceptionHandling(parseExpression(input),
				false, TestErrorHandler.INSTANCE,
				null, new EvalInfo(false));
	}
}
