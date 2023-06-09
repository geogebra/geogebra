package org.geogebra.common.main.exam;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilterFactory;
import org.geogebra.common.main.exam.restriction.ExamRestrictionModel;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class ExpressionFilterTest extends BaseUnitTest {
	@Test
	public void testMmsExpressionFilter() {
		assertNotNull(add("x + fractionalPart[5/2]"));
		ExpressionFilter filter = ExpressionFilterFactory.createMmsExpressionFilter();
		ExamRestrictionModel model = new ExamRestrictionModel();
		model.setExpressionFilter(filter);
		getApp().getParserFunctions().setExamRestrictionModel(model);
		assertNull(add("x + fractionalPart[5/2]"));
	}

	@Test
	public void testMmsExpressionFilterByName() {
		ExpressionFilter filter = ExpressionFilterFactory.createMmsExpressionFilter();
		assertFalse(filter.isAllowed(Operation.FRACTIONAL_PART));
	}

}
