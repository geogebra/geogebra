package org.geogebra.common.ownership;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A container for objects with global lifetime, i.e., objects that may live from
 * host app launch until host app termination.
 * <p>
 * <i>Note: By "host app", we mean the iOS/Android/Web app that hosts the GeoGebra code.</i>
 * </p><p>
 * This container serves as a home for objects with global lifetime that don't have a
 * direct owner (i.e., no "parent"). Try to put as few objects as possible in here.
 * </p>
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public final class GlobalScope {

	// Note: source order is initialization order!
	// https://stackoverflow.com/questions/4446088/java-in-what-order-are-static-final-fields-initialized

	public static final PropertiesRegistry propertiesRegistry = new DefaultPropertiesRegistry();
	public static final GeoElementPropertiesFactory geoElementPropertiesFactory =
			new GeoElementPropertiesFactory();
	public static final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();

	// intentionally assignable (for testing)
	public static ExamController examController = new ExamController(
			propertiesRegistry, geoElementPropertiesFactory, contextMenuFactory);

	/**
	 * @return The list of enabled (not-disabled) {@link SuiteSubApp}s in case an exam is currently active,
	 * or a list of all {@code SuiteSubApp} values otherwise.
	 */
	public static @Nonnull List<SuiteSubApp> getEnabledSubApps() {
		if (examController.isExamActive()) {
			return SuiteSubApp.availableValues().stream()
					.filter(subApp -> !examController.isDisabledSubApp(subApp))
					.collect(Collectors.toList());
		}
		return SuiteSubApp.availableValues();
	}

	/**
	 * Prevent instantiation.
	 */
	private GlobalScope() {
	}
}
