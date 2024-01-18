package org.geogebra.common.exam.restrictions;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamRegion;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.properties.Property;

/**
 * Represents restrictions that apply during an exam. Restrictions for the
 * different exam types are represented as subclasses of this class.
 * <p/>
 * All the different kinds of restrictions go in here:
 * <ul>
 * <li>disabled subapps,</li>
 * <li>disabled commands (see e.g. `CommandFilterFactory.createBayernCasFilter())`, and</li>
 * <li>disabled properties.</li>
 * </ul>
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

	private final Set<SuiteSubApp> disabledSubApps;
	private final SuiteSubApp defaultSubApp;
	private final ExpressionFilter expressionFilter;
	private final CommandFilter commandFilter;
	private final CommandArgumentFilter commandArgumentFilter;

	private final Set<Property> properties = new HashSet<>();
	private final Set<ExamRestrictable> restrictables = new HashSet<>();

	/**
	 * Prevent use of no-arg constructor.
	 */
	private ExamRestrictions() {
		this.disabledSubApps = null;
		this.defaultSubApp = null;
		this.expressionFilter = null;
		this.commandFilter = null;
		this.commandArgumentFilter = null;
	}

	/**
	 * Prevent instantiation, except by subclasses.
	 *
	 * @param disabledSubApps The set of disabled subapps for this exam type.
	 * @param defaultSubApp The subapp to activate at the start of an exam,
	 * if the current subapp is in the list of restricted subapps.
	 * @param expressionFilter An optional expression filter (e.g., ||) to apply during exams.
	 * @param commandFilter An optional command filter to apply during exams.
	 * @param commandArgumentFilter An optional command argument filter to apply during exams.
	 */
	protected ExamRestrictions(Set<SuiteSubApp> disabledSubApps,
			SuiteSubApp defaultSubApp,
			ExpressionFilter expressionFilter,
			CommandFilter commandFilter,
			CommandArgumentFilter commandArgumentFilter) {
		this.disabledSubApps = disabledSubApps;
		this.defaultSubApp = defaultSubApp;
		this.expressionFilter = expressionFilter;
		this.commandFilter = commandFilter;
		this.commandArgumentFilter = commandArgumentFilter;
	}

	/**
	 * @return The list of disabled (not allowed) subapps during exam, or `null` if there
	 * is no restriction on the available subapps.
	 */
	public final Set<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active at the time
	 * the exam starts, or `null` if there is no default.
	 */
	public final SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * Register a property that may be "frozen" for certain types of exams.
	 * It is safe to register any property - it will simply be ignored if none
	 * of the exam types places restrictions on this property.
	 *
	 * @param property A property that may be restricted ("frozen") during exams.
	 */
	public void registerProperty(Property property) {
		properties.add(property);
	}

	/**
	 * Register an object that may need to apply additional restrictions/customization
	 * for certain types of exams.
	 *
	 * @param restrictable An object that may need to perform additional customization
	 * when an exam is started.
	 */
	public void registerRestrictable(ExamRestrictable restrictable) {
		restrictables.add(restrictable);
	}

	/**
	 * Apply the restrictions.
	 *
	 * @param commandDispatcher The command dispatcher.
	 * @param algebraProcessor The algebra processor.
	 * TODO add more arguments if necessary
	 */
	public void apply(CommandDispatcher commandDispatcher, AlgebraProcessor algebraProcessor) {
		if (commandFilter != null) {
			commandDispatcher.addCommandFilter(commandFilter);
		}
		if (commandArgumentFilter != null) {
			commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
		}

		freeze(properties);

		restrictables.stream()
				.forEach(restrictable -> restrictable.applyRestrictions(this) );
	}

	/**
	 * Revert the changes from {@link #apply(CommandDispatcher, AlgebraProcessor)}.
	 *
	 * @param commandDispatcher The command dispatcher.
	 * @param algebraProcessor The algebra processor.
	 * TODO add more arguments if necessary
	 */
	public void unapply(CommandDispatcher commandDispatcher, AlgebraProcessor algebraProcessor) {
		if (commandFilter != null) {
			commandDispatcher.removeCommandFilter(commandFilter);
		}
		if (commandArgumentFilter != null) {
			commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
		}

		unfreeze(properties);

		restrictables.stream()
				.forEach(restrictable -> restrictable.unapplyRestrictions(this) );
	}

	/**
	 * "Freeze" certain properties (i.e. prevent changing the value, or triggering the action)
	 * at the start of the exam.
	 *
	 * @param properties The set of registered properties.
	 */
	protected void freeze(Set<Property> properties) {
	}

	/**
	 * "Unfreeze" certain properties at the end of the exam.
	 *
	 * @param properties The set of registered properties.
	 */
	protected void unfreeze(Set<Property> properties) {
	}
}
