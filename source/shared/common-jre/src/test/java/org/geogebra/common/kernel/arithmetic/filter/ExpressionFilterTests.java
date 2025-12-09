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

package org.geogebra.common.kernel.arithmetic.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Test;

public class ExpressionFilterTests extends BaseUnitTest {

	private final ErrorAccumulator errorAccumulator = new ErrorAccumulator();

	@Test
	public void testComplexExpression() throws Exception {
		ExpressionFilter filter = new ComplexExpressionFilter();
		ValidExpression expression = parse("3i");
		assertFalse(filter.isAllowed(expression));
	}

	@Test
	public void testRadianExpression() throws Exception {
		ExpressionFilter filter = new RadianGradianFilter();
		ValidExpression expression = parse("3rad");
		assertFalse(filter.isAllowed(expression));
	}

	@Test
	public void testOperationFilterPassing() {
		ExpressionFilter filter = e -> true;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		GeoElementND[] minusValue = process(algebraProcessor, "1-2");
		assertNotNull(minusValue);
		assertEquals("", errorAccumulator.getErrors());
	}

	@Test
	public void testOperationFilterRejecting() {
		ExpressionFilter filter = ev -> !ev.isOperation(Operation.PLUS);
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		process(algebraProcessor, "1+2");
		assertEquals("Sorry, something went wrong. Please check your input",
				errorAccumulator.getErrors());
	}

	@Test
	public void testNoListOperationsInScientific() {
		ExpressionFilter filter = ScientificOperationArgumentFilter.INSTANCE;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		process(algebraProcessor, "{1,2,3} + 3");
		assertEquals("Sorry, something went wrong. Please check your input",
				errorAccumulator.getErrors());
	}

	@Test
	public void testNoMatrixInScientific() {
		ExpressionFilter filter = ScientificOperationArgumentFilter.INSTANCE;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		process(algebraProcessor, "{{1,2},{3,4}}");
		assertEquals("Sorry, something went wrong. Please check your input",
				errorAccumulator.getErrors());
	}

	@Test
	public void testListArgumentsInScientific() {
		ExpressionFilter filter = ScientificOperationArgumentFilter.INSTANCE;
		AlgebraProcessor algebraProcessor = createAlgebraProcessor(filter);
		GeoElementND[] values = process(algebraProcessor, "mean({1,2,3}, {4,5,6})");
		assertThat(values[0].evaluateDouble(), is(2.1333333333333333));
		assertEquals("", errorAccumulator.getErrors());
	}

	private GeoElementND[] process(AlgebraProcessor ap, String input) {
		return ap.processAlgebraCommandNoExceptionHandling(parseExpression(input),
				false, errorAccumulator,
				null, new EvalInfo(false));
	}

	private ValidExpression parse(String input) throws Exception {
		return getKernel().getParser().parseGeoGebraExpression(input);
	}

	private AlgebraProcessor createAlgebraProcessor(ExpressionFilter filter) {
		AlgebraProcessor ap = new AlgebraProcessor(getKernel(),
				new CommandDispatcherJre(getKernel()));
		ap.addInputExpressionFilter(filter);
		return ap;
	}
}
