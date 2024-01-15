package org.geogebra.common.exam.restrictions;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;

public final class VlaanderenExamRestrictions extends ExamRestrictions {

	@Override
	public List<SuiteSubApp> getDisabledSubApps() {
		return Arrays.asList(SuiteSubApp.CAS);
	}

	@Override
	public SuiteSubApp getDefaultSubApp() {
		return SuiteSubApp.GRAPHING;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createVlaanderenFilter();
	}
}
