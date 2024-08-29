package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;

public final class IBExamRestrictions extends ExamRestrictions {

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
				null);
	}
}
