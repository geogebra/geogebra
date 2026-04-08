/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.exam;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamControllerDelegate;
import org.geogebra.common.exam.ExamControllerIntegration;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.restrictions.Restrictable;
import org.geogebra.common.restrictions.RestrictionsControllerDelegate;
import org.geogebra.web.full.main.AppWFull;

/**
 * Clutter-reducing wrapper for `ExamControllerIntegration` (common).
 */
public class ExamControllerIntegrationW {

	private static ExamControllerIntegration examControllerIntegration;
	private static GeoElementPropertiesFactory geoElementPropertiesFactory;

	/**
	 * Set up {@link ExamController} integration.
	 * @param suiteScope The {@link SuiteScope} for which to set up {@code ExamController}
	 * integration.
	 * @param examControllerDelegate The {@code ExamController} delegate.
	 */
	public static void setup(@Nonnull SuiteScope suiteScope,
			@Nonnull ExamControllerDelegate examControllerDelegate,
			@Nonnull RestrictionsControllerDelegate restrictionsControllerDelegate) {
		examControllerIntegration = new ExamControllerIntegration(
				suiteScope.examController,
				examControllerDelegate,
				suiteScope.restrictionsController,
				restrictionsControllerDelegate);
		geoElementPropertiesFactory = suiteScope.geoElementPropertiesFactory;
	}

	/**
	 * Activate the app. Manages registration and deregistration of {@code Restrictable}s,
	 * and applies restrictions to app dependencies.
	 * @param app The current app.
	 */
	public static void activate(AppWFull app) {
		assert examControllerIntegration != null;
		assert geoElementPropertiesFactory != null;
		List<Restrictable> restrictables = List.of(
				app, app.getEuclidianView1(), app.getConfig()
		);
		examControllerIntegration.activate(
				app,
				app.getLocalization(),
				app.getAutocompleteProvider(),
				geoElementPropertiesFactory,
				restrictables);
	}
}
