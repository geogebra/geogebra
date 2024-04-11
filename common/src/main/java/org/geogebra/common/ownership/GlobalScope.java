package org.geogebra.common.ownership;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;

/**
 * A container for objects with global lifetime, i.e., objects that may live from
 * host app launch until host app termination.
 * <p/>
 * <i>Note: By "host app", we mean the iOS/Android/Web app that hosts the GeoGebra code.</i>
 * <p/>
 * This container serves as a home for objects with global lifetime that don't have a
 * direct owner (i.e., no "parent"). Try to put as few objects as possible in here.
 */
public final class GlobalScope {

	// Note: source order is initialization order!
	// https://stackoverflow.com/questions/4446088/java-in-what-order-are-static-final-fields-initialized

	public static final PropertiesRegistry propertiesRegistry = new DefaultPropertiesRegistry();

	public static ExamController examController = new ExamController(propertiesRegistry);

	/**
	 * Prevent instantiation.
	 */
	private GlobalScope() {
	}
}
