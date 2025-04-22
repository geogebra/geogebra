package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

final class NiedersachsenExamRestrictions extends ExamRestrictions {

	NiedersachsenExamRestrictions() {
		super(ExamType.NIEDERSACHSEN,
				Set.of(G3D, SCIENTIFIC),
				GRAPHING,
				null,
				null,
				null,
				Set.of(new CommandNameFilter(true, Commands.CSolve, Commands.CSolutions)),
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
