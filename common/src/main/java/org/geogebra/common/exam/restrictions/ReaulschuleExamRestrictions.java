package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;

final class ReaulschuleExamRestrictions extends ExamRestrictions {

	ReaulschuleExamRestrictions() {
		super(ExamType.REALSCHULE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
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
}
