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

package org.geogebra.common.exam;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.restrictions.Restrictable;
import org.geogebra.common.restrictions.Restrictions.ContextDependencies;
import org.geogebra.common.restrictions.RestrictionsController;
import org.geogebra.common.restrictions.RestrictionsControllerDelegate;

/**
 * A wrapper class to reduce code duplication amongst client platforms.
 * <p>
 * Client host apps are expected to create one instance of this class per
 * {@link org.geogebra.common.ownership.SuiteScope SuiteScope} early during app startup,
 * somewhere after {@link GlobalScope#registerNewSuiteScope()} is called.
 * </p>
 */
public final class ExamControllerIntegration {

	private final ExamController examController;
	private final RestrictionsController restrictionsController;
	private Collection<Restrictable> restrictables = new ArrayList<>();

	/**
	 * Set up exam controller integration.
	 * @param examController The exam controller (owned by the {@code SuiteScope})
	 * @param examControllerDelegate The exam controller delegate
	 * @param restrictionsController The restrictions controller (owned by the {@code SuiteScope})
	 * @param restrictionsControllerDelegate The restrictions controller delegate
	 */
	public ExamControllerIntegration(
			@Nonnull ExamController examController,
			@Nonnull ExamControllerDelegate examControllerDelegate,
			@Nonnull RestrictionsController restrictionsController,
			@Nonnull RestrictionsControllerDelegate restrictionsControllerDelegate) {
		this.examController = examController;
		examController.delegate = examControllerDelegate;
		this.restrictionsController = restrictionsController;
		restrictionsController.delegate = restrictionsControllerDelegate;
	}

	/**
	 * Activate a context.
	 * @param app The newly active app.
	 * @param localization The localization.
	 * @param autocompleteProvider The autocomplete provider.
	 * @param geoElementPropertiesFactory The geo element properties factory.
	 * @param newRestrictables The new list of {@link Restrictable}s. Any previously activated
	 * {@code Restrictable}s will be unregistered from the {@link RestrictionsController}, before
	 * the new set of {@code Restrictable}s will be registered with the {@link RestrictionsController}.
	 */
	public void activate(@Nonnull App app,
			@Nonnull Localization localization,
			@CheckForNull AutocompleteProvider autocompleteProvider,
			@Nonnull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nonnull Collection<Restrictable> newRestrictables) {
		for (Restrictable restrictable : restrictables) {
			restrictionsController.unregisterRestrictable(restrictable);
		}
		restrictables.clear();
		examController.setActiveContext(
				new ContextDependencies(
						app.getKernel().getAlgoDispatcher(),
						app.getKernel().getAlgebraProcessor().getCommandDispatcher(),
						app.getKernel().getAlgebraProcessor(),
						app.appScope.propertiesRegistry,
						localization,
						app.getSettings(),
						app.getKernel().getStatisticGroupsBuilder(),
						autocompleteProvider,
						app,
						app.getKernel().getInputPreviewHelper(),
						app.getKernel().getConstruction(),
						geoElementPropertiesFactory,
						app)
		);
		restrictables.addAll(newRestrictables);
		for (Restrictable restrictable : restrictables) {
			restrictionsController.registerRestrictable(restrictable);
		}
	}
}
