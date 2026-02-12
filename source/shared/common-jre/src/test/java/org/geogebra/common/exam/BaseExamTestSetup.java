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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseExamTestSetup extends BaseAppTestSetup {
    protected PropertiesRegistry propertiesRegistry;
    protected GeoElementPropertiesFactory geoElementPropertiesFactory;
    protected ContextMenuFactory contextMenuFactory;
    protected ExamController examController;
    protected AutocompleteProvider autocompleteProvider;

    @BeforeEach
    void baseExamTestSetup() {
        // keep existing references, so we don't need to touch every test
        geoElementPropertiesFactory = suiteScope.geoElementPropertiesFactory;
        contextMenuFactory = suiteScope.contextMenuFactory;
        examController = suiteScope.examController;
    }

    @Override
    protected void setupApp(SuiteSubApp subApp) {
        super.setupApp(subApp);

        propertiesRegistry = getApp().appScope.propertiesRegistry;
        autocompleteProvider = new AutocompleteProvider(getApp(), false);
        examController.setActiveContext(
                getApp(),
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
                getKernel().getConstruction());
        examController.registerRestrictable(getApp());
    }
}
