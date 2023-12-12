package org.geogebra.common.exam.restrictions;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.exam.restriction.ExamRegion;

public final class GenericExamRestrictions extends ExamRestrictions {

	@Override
	public List<SuiteSubApp> getDisabledSubApps() {
		return null;
	}

	@Override
	public SuiteSubApp getDefaultSubApp() {
		return null;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return null;
	}
}
