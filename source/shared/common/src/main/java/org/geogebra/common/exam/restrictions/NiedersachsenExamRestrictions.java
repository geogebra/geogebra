package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;

import java.util.Set;

import org.geogebra.common.exam.ExamType;

final class NiedersachsenExamRestrictions extends ExamRestrictions {

	NiedersachsenExamRestrictions() {
		super(ExamType.NIEDERSACHSEN,
				Set.of(G3D, SCIENTIFIC),
				GRAPHING,
				null,
				null,
				null,
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
}
