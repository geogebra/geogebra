package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;

import java.util.Set;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

final class BayernCasExamRestrictions extends ExamRestrictions {

	BayernCasExamRestrictions() {
		super(ExamType.BAYERN_CAS,
				Set.of(GRAPHING, GEOMETRY, G3D, PROBABILITY, SCIENTIFIC),
				CAS,
				null,
				null,
				null,
				createCommandFilters(),
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

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Plane);
		return Set.of(nameFilter);
	}
}