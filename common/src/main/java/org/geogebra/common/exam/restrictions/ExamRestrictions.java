package org.geogebra.common.exam.restrictions;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.exam.restriction.ExamRegion;

/**
 * Restrictions that apply during an exam.
 */
public abstract class ExamRestrictions {

	public static ExamRestrictions forRegion(ExamRegion region) {
		switch (region) {
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	/**
	 * @return The list of disabled (not allowed) subapps during exam. Return `null` if there
	 * is no restriction on the available subapps.
	 */
	public abstract List<SuiteSubApp> getDisabledSubApps();

	/**
	 * @return The default subapp to switch to if a disabled subapp is active at the time
	 * the exam starts. Return `null` if there is no default.
	 */
	public abstract SuiteSubApp getDefaultSubApp();

	/**
	 * @return A filter for commands that should be supressed during the exam.
	 * Return `null` if no commands need to be supressed.
	 */
	public abstract CommandFilter getCommandFilter();

	/**
	 * Apply the restrictions.
	 * @param kernel
	 * @param commandDispatcher
	 */
	public void apply(Kernel kernel, CommandDispatcher commandDispatcher) {
	}

	/**
	 * Revert the changes from {@link #apply(Kernel, CommandDispatcher)}.
	 * @param kernel
	 * @param commandDispatcher
	 */
	public void unapply(Kernel kernel, CommandDispatcher commandDispatcher) {
	}
}
