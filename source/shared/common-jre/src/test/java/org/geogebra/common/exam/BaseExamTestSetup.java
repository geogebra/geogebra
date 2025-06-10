package org.geogebra.common.exam;

import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;

public abstract class BaseExamTestSetup extends BaseAppTestSetup {
    protected final PropertiesRegistry propertiesRegistry = new DefaultPropertiesRegistry();
    protected final GeoElementPropertiesFactory geoElementPropertiesFactory =
            new GeoElementPropertiesFactory();
    protected final ContextMenuFactory contextMenuFactory = new ContextMenuFactory();
    protected final ExamController examController = new ExamController(
            propertiesRegistry, geoElementPropertiesFactory, contextMenuFactory);
    protected AutocompleteProvider autocompleteProvider;

    @Override
    protected void setupApp(SuiteSubApp subApp) {
        super.setupApp(subApp);
        autocompleteProvider = new AutocompleteProvider(getApp(), false);
        examController.setActiveContext(
                getApp(),
                getKernel().getAlgoDispatcher(),
                getCommandDispatcher(),
                getAlgebraProcessor(),
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
