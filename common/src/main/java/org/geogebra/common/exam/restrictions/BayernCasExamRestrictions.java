package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamRegion;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.EnglishCommandFilter;
import org.geogebra.common.kernel.commands.selector.NameCommandFilter;

final class BayernCasExamRestrictions extends ExamRestrictions {

	BayernCasExamRestrictions() {
		super(ExamRegion.BAYERN_CAS,
				Set.of(SuiteSubApp.GRAPHING, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY),
				SuiteSubApp.CAS,
				null,
				null,
				BayernCasExamRestrictions.createCommandFilters(),
				null,
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		NameCommandFilter nameFilter = new NameCommandFilter(true);
		nameFilter.addCommands(Commands.Plane);
		return Set.of(new EnglishCommandFilter(nameFilter));
	}
}