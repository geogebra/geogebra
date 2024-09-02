package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.*;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;

public final class IBExamRestrictions extends ExamRestrictions {

	private static final Set<Dist> restrictedDistributions = Set.of(Dist.EXPONENTIAL, Dist.CAUCHY,
			Dist.WEIBULL, Dist.GAMMA, Dist.BETA, Dist.LOGNORMAL, Dist.LOGISTIC, Dist.PASCAL);

	IBExamRestrictions() {
		super(ExamType.IB,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D),
				SuiteSubApp.GRAPHING,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				createDistributionPropertyRestriction());
	}

	private static Map<String, PropertyRestriction> createDistributionPropertyRestriction() {
		return Map.of("Distribution", new PropertyRestriction(false, value ->
			!restrictedDistributions.contains(value)
		));
	}
}
