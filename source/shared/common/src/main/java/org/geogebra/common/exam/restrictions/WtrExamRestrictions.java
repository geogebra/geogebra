package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.filter.ComplexExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;

public class WtrExamRestrictions extends ExamRestrictions {
	WtrExamRestrictions() {
		super(ExamType.WTR,
				Set.of(GRAPHING, GEOMETRY, G3D, CAS, PROBABILITY),
				SCIENTIFIC,
				null,
				createInputExpressionFilters(),
				createOutputExpressionFilters(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	private static Set<ExpressionFilter> createInputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}

	private static Set<ExpressionFilter> createOutputExpressionFilters() {
		return Set.of(new ComplexExpressionFilter());
	}
}
