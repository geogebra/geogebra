package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

final class BayernCasExamRestrictions extends ExamRestrictions {

	BayernCasExamRestrictions() {
		super(ExamType.BAYERN_CAS,
				Set.of(SuiteSubApp.GRAPHING, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.CAS,
				null,
				null,
				null,
				createCommandFilters(),
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