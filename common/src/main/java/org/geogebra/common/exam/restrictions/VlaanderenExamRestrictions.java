package org.geogebra.common.exam.restrictions;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;

public final class VlaanderenExamRestrictions extends ExamRestrictions {

	public VlaanderenExamRestrictions() {
		super(Arrays.asList(SuiteSubApp.CAS),
				SuiteSubApp.GRAPHING,
				CommandFilterFactory.createVlaanderenFilter());
	}
}
