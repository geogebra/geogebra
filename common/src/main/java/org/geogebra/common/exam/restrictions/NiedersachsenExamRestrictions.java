package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamRegion;

final class NiedersachsenExamRestrictions extends ExamRestrictions {

	NiedersachsenExamRestrictions() {
		super(ExamRegion.NIEDERSACHSEN,
				Set.of(SuiteSubApp.G3D),
				SuiteSubApp.GRAPHING,
				null,
				null,
				null,
				null);
	}
}
