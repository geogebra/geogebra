package org.geogebra.common.exam.restrictions;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
 * Represents restrictions that apply during exams.
 * <p/>
 * Restrictions that are specific to the different exam types are represented as subclasses
 * of this class.
 * Restrictions that apply to all exam types should be implemented in this class
 * (in {@link #apply(CommandDispatcher, AlgebraProcessor, PropertiesRegistry, Object)}).
 * <p/>
 * Any restrictions to be applied during exams should be implemented in here (so that
 * everything is one place):
 * <ul>
 * <li>disabled subapps,</li>
 * <li>restricted commands,</li>
 * <li>restricted expressions,</li>
 * <li>frozen properties,</li>
 * <li>etc.</li>
 * </ul>
 */
public class ExamRestrictions {

	public static ExamRestrictions forExamType(ExamRegion examType) {
		switch (examType) {
		case BAYERN_CAS:
			return new BayernCasExamRestrictions();
//		case MMS:
//			return new MmsExamRestrictions();
		case NIEDERSACHSEN:
			return new NiedersachsenExamRestrictions();
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
	}

	private final ExamRegion examType;
	private final Set<SuiteSubApp> disabledSubApps;
	private final SuiteSubApp defaultSubApp;
	private final Set<ExpressionFilter> expressionFilters;
	private final Set<CommandFilter> commandFilters;
	private final Set<CommandArgumentFilter> commandArgumentFilters;
	// filter independent of exam region
	private final CommandArgumentFilter examCommandArgumentFilter =
			new ExamCommandArgumentFilter();
	private final Set<String> frozenProperties;

	/**
	 * Prevent instantiation, except by subclasses.
	 * @param examType The exam type.
	 * @param disabledSubApps The set of disabled subapps for this exam type.
	 * @param defaultSubApp The subapp to activate at the start of an exam,
	 * if the current subapp is in the list of restricted subapps.
	 * @param expressionFilters An optional set of expression filters (e.g., ||) to apply during exams.
	 * @param commandFilters An optional command filter to apply during exams.
	 * @param commandArgumentFilters An optional command argument filter to apply during exams.
	 */
	protected ExamRestrictions(@Nonnull ExamRegion examType,
			@Nullable Set<SuiteSubApp> disabledSubApps,
			@Nullable SuiteSubApp defaultSubApp,
			@Nullable Set<ExpressionFilter> expressionFilters,
			@Nullable Set<CommandFilter> commandFilters,
			@Nullable Set<CommandArgumentFilter> commandArgumentFilters,
			@Nullable Set<String> frozenProperties) {
		this.examType = examType;
		this.disabledSubApps = disabledSubApps;
		this.defaultSubApp = defaultSubApp != null ? defaultSubApp : SuiteSubApp.GRAPHING;
		this.expressionFilters = expressionFilters;
		this.commandFilters = commandFilters;
		this.commandArgumentFilters = commandArgumentFilters;
		this.frozenProperties = frozenProperties;
	}

	/**
	 * @return The list of disabled (i.e., not allowed) subapps during exams, or `null` if there
	 * is no restriction on the available subapps.
	 */
	public final @CheckForNull Set<SuiteSubApp> getDisabledSubApps() {
		return disabledSubApps;
	}

	/**
	 * @return The default subapp to switch to if a disabled subapp is active when
	 * the exam starts, or `null` if there is no default.
	 */
	public final @Nonnull SuiteSubApp getDefaultSubApp() {
		return defaultSubApp;
	}

	/**
	 * Apply the exam restrictions.
	 */
	public void apply(@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context) {
		if (commandDispatcher != null) {
			if (commandFilters != null) {
				for (CommandFilter commandFilter : commandFilters) {
					commandDispatcher.addCommandFilter(commandFilter);
				}
			}
			commandDispatcher.addCommandArgumentFilter(examCommandArgumentFilter);
			if (commandArgumentFilters != null) {
				for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
					commandDispatcher.addCommandArgumentFilter(commandArgumentFilter);
				}
			}
		}
		if (algebraProcessor != null) {
			if (expressionFilters != null) {
				for (ExpressionFilter expressionFilter : expressionFilters) {
					algebraProcessor.addExpressionFilter(expressionFilter);
				}
			}
		}
		if (frozenProperties != null) {
			for (String frozenProperty : frozenProperties) {
				Property property = propertiesRegistry.lookup(frozenProperty, context);
				if (property != null) {
					freeze(property);
				}
			}
		}
	}

	/**
	 * Revert the changes from {@link #apply(CommandDispatcher, AlgebraProcessor, PropertiesRegistry, Object)}.
	 */
	public void revert(@Nullable CommandDispatcher commandDispatcher,
			@Nullable AlgebraProcessor algebraProcessor,
			@Nullable PropertiesRegistry propertiesRegistry,
			@Nullable Object context) {
		if (commandDispatcher != null) {
			if (commandFilters != null) {
				for (CommandFilter commandFilter : commandFilters) {
					commandDispatcher.removeCommandFilter(commandFilter);
				}
			}
			commandDispatcher.removeCommandArgumentFilter(examCommandArgumentFilter);
			if (commandArgumentFilters != null) {
				for (CommandArgumentFilter commandArgumentFilter : commandArgumentFilters) {
					commandDispatcher.removeCommandArgumentFilter(commandArgumentFilter);
				}
			}
		}
		if (algebraProcessor != null) {
			if (expressionFilters != null) {
				for (ExpressionFilter expressionFilter : expressionFilters) {
					algebraProcessor.removeExpressionFilter(expressionFilter);
				}
			}
		}
		if (frozenProperties != null) {
			for (String frozenProperty : frozenProperties) {
				Property property = propertiesRegistry.lookup(frozenProperty, context);
				if (property != null) {
					unfreeze(property);
				}
			}
		}
	}

	/**
	 * Handles lazy property instantiation/registration.
	 * @param property A property that just got registered.
	 */
	public void propertyRegistered(@Nonnull Property property) {
		if (frozenProperties != null && frozenProperties.contains(property.getRawName())) {
			freeze(property);
		}
	}

	/**
	 * Unfreezes a property
	 * @param property
	 */
	public void propertyUnregistered(@Nonnull Property property) {
		if (frozenProperties != null && frozenProperties.contains(property.getRawName())) {
			unfreeze(property);
		}
	}

	/**
	 * "Freeze" a property (i.e. prevent changing the value, or triggering the action)
	 * at the start of the exam.
	 * @param property A property.
	 */
	protected void freeze(@Nonnull Property property) {
		property.setFrozen(true);
		if (property instanceof ValuedProperty) {
			freezeValue((ValuedProperty) property);
		}
	}

	/**
	 * "Unfreeze" a property at the end of the exam.
	 * @param property A property.
	 */
	protected void unfreeze(@Nonnull Property property) {
		property.setFrozen(false);
		if (property instanceof ValuedProperty) {
			freezeValue((ValuedProperty) property);
		}
	}

	/**
	 * Override to freeze the value of a property to some fixed value.
	 * @param property A property whose value should be fixed during an exam.
	 */
	protected void freezeValue(@Nonnull ValuedProperty property) {
	}

	/**
	 * Override to unfreeze the value of a property.
	 * @param property A property should be fixed during an exam.
	 */
	protected void unfreezeValue(@Nonnull  ValuedProperty property) {
	}

	// TODO unclear how to implement
	// see https://git.geogebra.org/ggb/geogebra/-/issues/8#function-graphs-new
	public boolean isSelectionAllowed(GeoElementND geoND) {
		return true;
	}
}
