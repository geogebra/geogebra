package org.geogebra.common.exam.restrictions;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.exam.restriction.ExamRegion;

public abstract class ExamRestrictions {

	public static ExamRestrictions forRegion(ExamRegion region) {
		switch (region) {
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	public abstract List<SuiteSubApp> getDisabledSubApps();

	public abstract SuiteSubApp getDefaultSubApp();

	public abstract CommandFilter getCommandFilter();

	public void apply(Kernel kernel, CommandDispatcher commandDispatcher) {
	}

	public void unapply(Kernel kernel, CommandDispatcher commandDispatcher) {
	}
}
