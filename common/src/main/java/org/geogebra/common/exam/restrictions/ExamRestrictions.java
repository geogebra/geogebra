package org.geogebra.common.exam.restrictions;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamRegion;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ExamCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.ValuedProperty;

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

	public static ExamRestrictions forExamType(ExamRegion examType) {
		switch (examType) {
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	private final ExamRegion examType;
	private final Set<SuiteSubApp> disabledSubApps;
	private final SuiteSubApp defaultSubApp;
	private final ExpressionFilter expressionFilter;
	private final CommandFilter commandFilter;
	private final CommandArgumentFilter commandArgumentFilter;
	// independent of exam region
	private final ExamCommandArgumentFilter examCommandArgumentFilter = new ExamCommandArgumentFilter();

	private final Set<String> frozenProperties;

	/**
	 * Prevent use of no-arg constructor.
	 */
	private ExamRestrictions() {
		this.examType = null;
		this.disabledSubApps = null;
		this.defaultSubApp = null;
		this.expressionFilter = null;
		this.commandFilter = null;
		this.commandArgumentFilter = null;
		this.frozenProperties = null;
	}

	/**
	 * Prevent instantiation, except by subclasses.
	 *
	 * @param examType The exam type.
	 * @param disabledSubApps The set of disabled subapps for this exam type.
	 * @param defaultSubApp The subapp to activate at the start of an exam,
	 * if the current subapp is in the list of restricted subapps.
	 * @param expressionFilter An optional expression filter (e.g., ||) to apply during exams.
	 * @param commandFilter An optional command filter to apply during exams.
	 * @param commandArgumentFilter An optional command argument filter to apply during exams.
	 */
	protected ExamRestrictions(ExamRegion examType,
							   Set<SuiteSubApp> disabledSubApps,
							   SuiteSubApp defaultSubApp,
							   ExpressionFilter expressionFilter,
							   CommandFilter commandFilter,
							   CommandArgumentFilter commandArgumentFilter,
							   Set<String> frozenProperties) {
		this.examType = examType;
		this.disabledSubApps = disabledSubApps;
		this.defaultSubApp = defaultSubApp != null ? defaultSubApp : SuiteSubApp.GRAPHING;
		this.expressionFilter = expressionFilter;
		this.commandFilter = commandFilter;
		this.commandArgumentFilter = commandArgumentFilter;
		this.frozenProperties = frozenProperties;
	}

	public ExamRegion getExamType() { return examType; }

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
	 * Apply the restrictions.
	 *
	 * TODO add more arguments if necessary
	 */
	public final void apply(CommandDispatcher commandDispatcher,
			AlgebraProcessor algebraProcessor,
			PropertiesRegistry propertiesRegistry,
			Object context) {
		if  (commandDispatcher != null) {
			if (commandFilter != null) {
				commandDispatcher.addCommandFilter(commandFilter);
			}
			commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
			if (commandArgumentFilter != null) {
				commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			if (expressionFilter != null) {
				algebraProcessor.addExpressionFilter(expressionFilter);
			}
		}
		for (String frozenProperty : frozenProperties) {
			Property property = propertiesRegistry.lookup(frozenProperty, context);
			if (property != null) {
				freeze(property);
			}
		}
	}

	/**
	 * Revert the changes from {@link #apply(CommandDispatcher, AlgebraProcessor, PropertiesRegistry, Object)}.
	 */
	public final void unapply(CommandDispatcher commandDispatcher,
			AlgebraProcessor algebraProcessor,
			PropertiesRegistry propertiesRegistry,
			Object context) {
		if (commandDispatcher != null) {
			if (commandFilter != null) {
				commandDispatcher.removeCommandFilter(commandFilter);
			}
			commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
			if (commandArgumentFilter != null) {
				commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
			}
		}
		if (algebraProcessor != null) {
			if (expressionFilter != null) {
				algebraProcessor.removeExpressionFilter(expressionFilter);
			}
		}
		for (String frozenProperty : frozenProperties) {
			Property property = propertiesRegistry.lookup(frozenProperty, context);
			if (property != null) {
				unfreeze(property);
			}
		}
	}

	/**
	 * Handles lazy property instantiation/registration.
	 *
	 * @param property A property that just got registered.
	 */
	public void propertyRegistered(Property property) {
		if (frozenProperties.contains(property.getRawName())) {
			freeze(property);
		}
	}

	/**
	 * Unfreezes a property
	 * @param property
	 */
	public void propertyUnregistered(Property property) {
		if (frozenProperties.contains(property.getRawName())) {
			unfreeze(property);
		}
	}

	/**
	 * "Freeze" a property (i.e. prevent changing the value, or triggering the action)
	 * at the start of the exam.
	 *
	 * @param property A property.
	 */
	protected void freeze(Property property) {
		property.freeze();
		if (property instanceof ValuedProperty) {
			freezeValue((ValuedProperty) property);
		}
	}

	/**
	 * "Unfreeze" a property at the end of the exam.
	 *
	 * @param property A property.
	 */
	protected void unfreeze(Property property) {
		property.unfreeze();
		if (property instanceof ValuedProperty) {
			freezeValue((ValuedProperty) property);
		}
	}

	/**
	 * Override to freeze the value of a property.
	 *
	 * @param property A property whose value should be fixed during an exam.
	 */
	protected void freezeValue(ValuedProperty property) {
	}

	/**
	 * Override to unfreeze the value of a property.
	 *
	 * @param property A property should be fixed during an exam.
	 */
	protected  void unfreezeValue(ValuedProperty property) {
	}

	public boolean isSelectionAllowed(GeoElementND geoND) {
		return true;
	}
}
