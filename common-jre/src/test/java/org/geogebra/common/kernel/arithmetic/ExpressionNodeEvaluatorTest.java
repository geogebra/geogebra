package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.arithmetic.filter.ScientificOperationArgumentFilter;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class ExpressionNodeEvaluatorTest extends BaseUnitTest {

	@Test
	public void testOperationFilterPassing() {
		OperationArgumentFilter filter = Mockito.mock(OperationArgumentFilter.class);
		Mockito.when(filter.isAllowed(Mockito.any(Operation.class),
				Mockito.any(ExpressionValue.class),
				Mockito.any(ExpressionValue.class))).thenReturn(true);
		ExpressionNodeEvaluator evaluator = createEvaluator(filter);
		ExpressionNode minusExpression = parseExpression("1-2");
		ExpressionValue minusValue = evaluator.evaluate(minusExpression,
				StringTemplate.defaultTemplate);
		Assert.assertNotNull(minusValue);
	}

	@Test(expected = MyError.class)
	public void testOperationFilterRejecting() {
		OperationArgumentFilter filter = Mockito.mock(OperationArgumentFilter.class);
		Mockito.doReturn(false).when(filter).isAllowed(Mockito.eq(Operation.PLUS),
				Mockito.any(ExpressionValue.class),
				Mockito.any(ExpressionValue.class));
		ExpressionNodeEvaluator evaluator = createEvaluator(filter);
		ExpressionNode plusExpression = parseExpression("1+2");
		evaluator.evaluate(plusExpression, StringTemplate.defaultTemplate);
	}

	private ExpressionNodeEvaluator createEvaluator(OperationArgumentFilter filter) {
		return new ExpressionNodeEvaluator(getApp().getLocalization(), getKernel(), filter);
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

	@Test
	public void testIsSimpleNumber() {
		ExpressionNode minusOne = parseExpression("-1");
		assertThat(minusOne, notNullValue());
		assertThat(minusOne.isSimpleNumber(), is(true));

		ExpressionNode recurringDecimal = parseExpression("1.3" + Unicode.OVERLINE);
		assertThat(recurringDecimal, notNullValue());
		assertThat(recurringDecimal.isSimpleNumber(), is(false));
	}

	@Test
	public void testCalculationWithMinusOneIsNotSimpleNumber() {
		ExpressionNode minusOneCalc = parseExpression("(-1)(3)");
		assertThat(minusOneCalc, notNullValue());
		assertThat(minusOneCalc.isSimpleNumber(), is(false));
	}

	@Test(expected = MyError.class)
	public void testNoListOperationsInScientific() {
		OperationArgumentFilter filter = new ScientificOperationArgumentFilter();
		ExpressionNodeEvaluator evaluator = createEvaluator(filter);
		ExpressionNode listExpression = parseExpression("{1,2,3} + 3");
		evaluator.evaluate(listExpression, StringTemplate.defaultTemplate);
	}

	@Test
	public void testListArgumentsInScientific() {
		OperationArgumentFilter filter = new ScientificOperationArgumentFilter();
		ExpressionNodeEvaluator evaluator = createEvaluator(filter);
		ExpressionNode listExpression = parseExpression("mean({1,2,3}, {4,5,6})");
		ExpressionValue mean =
				evaluator.evaluate(listExpression, StringTemplate.defaultTemplate);
		assertThat(mean.evaluateDouble(), is(2.1333333333333333));
	}
}
