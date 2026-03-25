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

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.restrictions.Restrictions.ContextDependencies;
import org.geogebra.common.restrictions.RestrictionsController;
import org.geogebra.common.restrictions.RestrictionsControllerDelegate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseExamTestSetup extends BaseAppTestSetup
		implements RestrictionsControllerDelegate {

	protected ExamController examController;
	protected RestrictionsController restrictionsController;
	protected PropertiesRegistry propertiesRegistry;
	protected GeoElementPropertiesFactory geoElementPropertiesFactory;
	protected AutocompleteProvider autocompleteProvider;
	protected Material activeMaterial;
	protected boolean didRequestClearApps = false;
	protected boolean didRequestClearClipboard = false;

	@BeforeEach
	void baseExamTestSetup() {
		geoElementPropertiesFactory = suiteScope.geoElementPropertiesFactory;
		examController = suiteScope.examController;
		restrictionsController = suiteScope.restrictionsController;
		restrictionsController.delegate = this;

		activeMaterial = null;
		didRequestClearApps = false;
		didRequestClearClipboard = false;
	}

	@Override
	protected void setupApp(SuiteSubApp subApp) {
		super.setupApp(subApp);

		propertiesRegistry = getApp().appScope.propertiesRegistry;
		autocompleteProvider = new AutocompleteProvider(getApp(), false);
		examController.setActiveContext(
				new ContextDependencies(
						getKernel().getAlgoDispatcher(),
						getCommandDispatcher(),
						getAlgebraProcessor(),
						propertiesRegistry,
						getApp().getLocalization(),
						getApp().getSettings(),
						getKernel().getStatisticGroupsBuilder(),
						autocompleteProvider,
						getApp(),
						getKernel().getInputPreviewHelper(),
						getKernel().getConstruction(),
						geoElementPropertiesFactory,
						getApp()));
		restrictionsController.registerRestrictable(getApp());
	}

	protected void startExam(ExamType examType) {
		examController.startExam(examType, null);
	}

	// -- RestrictionsControllerDelegate -

	@Override
	public @CheckForNull SuiteSubApp getCurrentSubApp() {
		AppConfig config = getApp().getConfig();
		String appCode = Objects.equals(config.getAppCode(), GeoGebraConstants.SUITE_APPCODE)
				? config.getSubAppCode() : config.getAppCode();
		if (appCode != null) {
			return SuiteSubApp.forCode(appCode);
		}
		return null;
	}

	@Override
	public void switchSubApp(@Nonnull SuiteSubApp subApp) {
		if (!subApp.equals(getCurrentSubApp())) {
			setupApp(subApp);
		}
	}
}
