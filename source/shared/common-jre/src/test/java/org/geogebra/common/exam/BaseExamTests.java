package org.geogebra.common.exam;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Before;

public abstract class BaseExamTests implements ExamControllerDelegate {

    protected final PropertiesRegistry propertiesRegistry =
            new DefaultPropertiesRegistry();
    protected final GeoElementPropertiesFactory geoElementPropertiesFactory =
            new GeoElementPropertiesFactory();
    protected final ContextMenuFactory contextMenuFactory =
            new ContextMenuFactory();
    protected final ExamController examController =
            new ExamController(propertiesRegistry, geoElementPropertiesFactory, contextMenuFactory);

    protected final List<ExamState> examStates = new ArrayList<>();
    protected final ErrorAccumulator errorAccumulator = new ErrorAccumulator();
    protected boolean didRequestClearApps = false;
    protected boolean didRequestClearClipboard = false;

    protected AppCommon app;
    protected CommandDispatcher commandDispatcher;
    protected CommandDispatcher previousCommandDispatcher;
    protected AlgebraProcessor algebraProcessor;
    protected SuiteSubApp currentSubApp;
    protected AutocompleteProvider autocompleteProvider;
    protected Material activeMaterial;

    private AppConfig createConfig(SuiteSubApp subApp) {
        switch (subApp) {
            case CAS:
                return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
            case GRAPHING:
                return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
            case GEOMETRY:
                return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
            case SCIENTIFIC:
                break;
            case G3D:
                return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
            case PROBABILITY:
                return new AppConfigProbability(GeoGebraConstants.SUITE_APPCODE);
        }
        return null;
    }

    protected void switchApp(SuiteSubApp subApp) {
        // keep references so that we can check if restrictions have been reverted correctly
        previousCommandDispatcher = commandDispatcher;

        currentSubApp = subApp;
        app = AppCommonFactory.create(createConfig(subApp));
        activeMaterial = null;
        algebraProcessor = app.getKernel().getAlgebraProcessor();
        commandDispatcher = algebraProcessor.getCommandDispatcher();
        autocompleteProvider = new AutocompleteProvider(app, false);
        examController.setActiveContext(app, commandDispatcher, algebraProcessor,
                app.getLocalization(), app.getSettings(), autocompleteProvider, app,
                app.getKernel().getInputPreviewHelper());
    }

    protected void setInitialApp(SuiteSubApp subApp) {
        currentSubApp = subApp;
        app = AppCommonFactory.create(createConfig(subApp));
        algebraProcessor = app.getKernel().getAlgebraProcessor();
        commandDispatcher = algebraProcessor.getCommandDispatcher();
        autocompleteProvider = new AutocompleteProvider(app, false);
        examController.setActiveContext(app, commandDispatcher, algebraProcessor,
                app.getLocalization(), app.getSettings(), autocompleteProvider, app,
                app.getKernel().getInputPreviewHelper());
    }

    protected GeoElementND[] evaluate(String expression) {
        EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
        return algebraProcessor.processAlgebraCommandNoExceptionHandling(
                expression, false, errorAccumulator, evalInfo, null);
    }

    protected void editGeoElement(GeoElement geoElement, String newExpression) {
        EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForRedefinition(
                app.getKernel(), geoElement, true);
        algebraProcessor.changeGeoElementNoExceptionHandling(
                geoElement, newExpression, evalInfo, false, null, ErrorHelper.silent());
    }

    protected GeoElement evaluateGeoElement(String expression) {
        return (GeoElement) evaluate(expression)[0];
    }

    @Before
    public void setUp() {
        examController.setDelegate(this);
        examController.addListener(examStates::add);
    }

    // ExamControllerDelegate

    @Override
    public void examClearApps() {
        activeMaterial = null;
        didRequestClearApps = true;
    }

    @Override
    public void examClearClipboard() {
        didRequestClearClipboard = true;
    }

    @Override
    public void examSetActiveMaterial(@Nullable Material material) {
        activeMaterial = material;
    }

    @CheckForNull
    @Override
    public Material examGetActiveMaterial() {
        return activeMaterial;
    }

    @CheckForNull
    @Override
    public SuiteSubApp examGetCurrentSubApp() {
        return currentSubApp;
    }

    @Override
    public void examSwitchSubApp(@Nonnull SuiteSubApp subApp) {
        if (!subApp.equals(currentSubApp)) {
            switchApp(subApp);
        }
    }
}
