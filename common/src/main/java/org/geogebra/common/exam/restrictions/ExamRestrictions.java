package org.geogebra.common.exam.restrictions;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.exam.ExamRegion;

/**
 * Restrictions that apply during an exam.
 */
public class ExamRestrictions {

	public static ExamRestrictions forRegion(ExamRegion region) {
		switch (region) {
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	private final List<SuiteSubApp> disabledSubApps;
	private final SuiteSubApp defaultSubApp;
	private final CommandFilter commandFilter;

	/**
	 * Prevent use of no-arg constructor.
	 */
	private ExamRestrictions() {
		this.disabledSubApps = null;
		this.defaultSubApp = null;
		this.commandFilter = null;
	}

	public ExamRestrictions(List<SuiteSubApp> disabledSubApps,
			SuiteSubApp defaultSubApp,
			CommandFilter commandFilter) {
		this.disabledSubApps = disabledSubApps;
		this.defaultSubApp = defaultSubApp;
		this.commandFilter = commandFilter;
	}

	/**
	 * @return The list of disabled (not allowed) subapps during exam. Return `null` if there
	 * is no restriction on the available subapps.
	 */
	public final List<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active at the time
	 * the exam starts. Return `null` if there is no default.
	 */
	public final SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * @return A filter for commands that should be supressed during the exam.
	 * Return `null` if no commands need to be supressed.
	 */
	public final CommandFilter getCommandFilter() {
		return commandFilter;
	}

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
