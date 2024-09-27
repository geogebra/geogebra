package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerTests implements ExamControllerDelegate {

	private ExamController examController;
	private PropertiesRegistry propertiesRegistry;
	private AppCommon app;
	private CommandDispatcher commandDispatcher;
	private CommandDispatcher previousCommandDispatcher;
	private AlgebraProcessor algebraProcessor;
	private SuiteSubApp currentSubApp;
	private AutocompleteProvider autocompleteProvider;
	private final List<ExamState> examStates = new ArrayList<>();
	private boolean didRequestClearApps;
	private boolean didRequestClearClipboard;
	private Material activeMaterial;

	@Before
	public void setUp() {
		app = null;
		algebraProcessor = null;
		commandDispatcher = null;

		propertiesRegistry = new DefaultPropertiesRegistry();

		examController = new ExamController(propertiesRegistry);
		examController.setDelegate(this);
		examController.addListener(examStates::add);
		examStates.clear();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		activeMaterial = null;
	}

	// Helpers

	private void setInitialApp(SuiteSubApp subApp) {
		currentSubApp = subApp;
		app = AppCommonFactory.create(createConfig(subApp));
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		commandDispatcher = algebraProcessor.getCommandDispatcher();
		autocompleteProvider = new AutocompleteProvider(app, false);
		propertiesRegistry.register(new AngleUnitProperty(app.getKernel(), app.getLocalization()),
				app);
		examController.setActiveContext(app, commandDispatcher, algebraProcessor,
				app.getLocalization(), app.getSettings(), autocompleteProvider, app);
	}

	private void switchApp(SuiteSubApp subApp) {
		// keep references so that we can check if restrictions have been reverted correctly
		previousCommandDispatcher = commandDispatcher;

		currentSubApp = subApp;
		app = AppCommonFactory.create(createConfig(subApp));
		activeMaterial = null;
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		commandDispatcher = algebraProcessor.getCommandDispatcher();
		autocompleteProvider = new AutocompleteProvider(app, false);
		propertiesRegistry.register(new AngleUnitProperty(app.getKernel(), app.getLocalization()),
				app);
		examController.setActiveContext(app, commandDispatcher, algebraProcessor,
				app.getLocalization(), app.getSettings(), autocompleteProvider, app);
	}

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

	private GeoElementND[] evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, null, evalInfo, null);
	}

	// State & duration

	@Test
	public void testPrepareExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		assertEquals(ExamState.IDLE, examController.getState());
		examController.prepareExam();

		assertNull(examController.getStartDate()); // not yet started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.PREPARING, examController.getState());
		assertEquals(List.of(ExamState.PREPARING), examStates);
		assertFalse(didRequestClearApps);
		assertFalse(didRequestClearClipboard);
		assertNull(activeMaterial);
	}

	@Test
	public void testStartExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);

		assertNotNull(examController.getStartDate()); // started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE), examStates);
		assertTrue(didRequestClearApps);
		assertTrue(didRequestClearClipboard);
		assertNotNull(activeMaterial);
	}

	@Test
	public void testStartExamWithoutActiveContext() {
		examController.prepareExam();
		assertThrows("starting exam without calling setActiveContext() should throw",
				IllegalStateException.class,
				() -> examController.startExam(ExamType.GENERIC, null));
	}

	// start exam without calling prepare() first (e.g., in crash recovery)
	@Test
	public void testStartExamWithoutPrepare() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.VLAANDEREN, null);
		assertEquals(ExamState.ACTIVE, examController.getState());
	}

	@Test
	public void testFinishExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		examController.finishExam();

		assertNotNull(examController.getStartDate()); // started
		assertNotNull(examController.getFinishDate()); // ended
		assertNotNull(examController.getExamSummary(app.getConfig(), app.getLocalization()));
		assertEquals(ExamState.FINISHED, examController.getState());
		assertEquals(Arrays.asList(
				ExamState.PREPARING,
				ExamState.ACTIVE,
				ExamState.FINISHED), examStates);
	}

	@Test
	public void testExitExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		examController.finishExam();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		examController.exitExam();

		assertNull(examController.getStartDate());
		assertNull(examController.getFinishDate());
		assertEquals(ExamState.IDLE, examController.getState()); // back to initial state
		assertEquals(Arrays.asList(
				ExamState.PREPARING,
				ExamState.ACTIVE,
				ExamState.FINISHED,
				ExamState.IDLE), examStates);
		assertTrue(didRequestClearApps);
		assertTrue(didRequestClearClipboard);
		assertNull(activeMaterial);
	}

	// Restrictions

	@Test
	public void testRestrictedSubApp() {
		setInitialApp(SuiteSubApp.CAS);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null); // doesn't allow CAS
		assertEquals(SuiteSubApp.GRAPHING, currentSubApp);
		assertNotNull(activeMaterial);
	}

	@Test
	public void testNonRestrictedSubApp() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertEquals(SuiteSubApp.GRAPHING, currentSubApp);
		assertNotNull(activeMaterial);
	}

	@Test
	public void testRestrictions() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();

		examController.setExamRestrictionsForTesting(
				new TestExamRestrictions(ExamType.VLAANDEREN));
		examController.startExam(ExamType.VLAANDEREN, null);

		// feature restrictions
		assertTrue(examController
				.isFeatureRestricted(ExamFeatureRestriction.DATA_TABLE_REGRESSION));
		// command restrictions
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		// TODO commandArgumentFilters
		// expression restrictions
		assertNull(evaluate("true || false"));
		// property restrictions
		Property angleUnit = propertiesRegistry.lookup("AngleUnit", app);
		assertNotNull(angleUnit);
		assertTrue("Angle unit should be frozen", angleUnit.isFrozen());

		examController.finishExam();
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		examController.exitExam();
		assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
	}

	@Test
	public void testSwitchToRestrictedSubApp() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();

		examController.setExamRestrictionsForTesting(
				ExamRestrictions.forExamType(ExamType.BAYERN_CAS)); // only allows CAS app
		examController.startExam(ExamType.BAYERN_CAS, null);
		assertEquals(SuiteSubApp.CAS, currentSubApp);
		assertNotNull(activeMaterial);
	}

	@Test
	public void testRestrictionsWhenSwitchingApps() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));

		switchApp(SuiteSubApp.GEOMETRY);
		// restrictions should be reverted on the previous (Graphing app) command dispatcher...
		assertTrue(previousCommandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		// ...and applied to the new (Geometry app) command dispatcher
		assertFalse(commandDispatcher.isAllowedByCommandFilters(Commands.Derivative));
		assertNotNull(activeMaterial);
	}

	@Test
	public void testLanguagePropertyDisabledDuringExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.GENERIC, null);

		GlobalScope.examController = examController;
		LanguageProperty languageProperty = new LanguageProperty(app, app.getLocalization());
		assertFalse(languageProperty.isEnabled()); // should be disabled during exam
	}

	@Test
	public void testCvteRestrictions() {
		setInitialApp(SuiteSubApp.GEOMETRY);
		examController.prepareExam();
		examController.startExam(ExamType.CVTE, null);

		// subapps restricted to Graphing, CAS disabled for this exam type
		assertEquals(SuiteSubApp.GRAPHING, currentSubApp);
		assertFalse(app.getSettings().getCasSettings().isEnabled());

		// check syntax restrictions on AutoCompleteProvider
		// - allow only Circle(<Center>, <Radius>) syntax
		List<AutocompleteProvider.Completion> completions = autocompleteProvider
				.getCompletions("circle").collect(Collectors.toList());
		assertEquals(1, completions.size());
		AutocompleteProvider.Completion circleCompletion = completions.get(0);
		assertEquals(1, circleCompletion.syntaxes.size());
		assertEquals("Circle( <Point>, <Radius Number> )", circleCompletion.syntaxes.get(0));

		// check syntax restrictions on CommandDispatcher
		// - (indirectly) via checkIsAllowedByCommandArgumentFilters
		evaluate("A=(1,1)");
		evaluate("B=(2,2)");
		GeoElementND[] circlePointPoint = evaluate("Circle(A, B)");
		assertNull(circlePointPoint);
		GeoElementND[] circlePointRadius = evaluate("Circle(A, 1)");
		assertNotNull(circlePointRadius);

		// check tool restrictions
		ToolCollection availableTools = app.getAvailableTools();
		assertTrue(availableTools.contains(EuclidianConstants.MODE_MOVE));
		assertFalse(availableTools.contains(EuclidianConstants.MODE_POINT));
	}

	@Test
	public void testDerivativeOperatorDisabledForVlaanderen() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamType.VLAANDEREN, null);

		assertNotNull(evaluate("f(x) = x^2"));
		assertNull(evaluate("f'"));
	}

	// -- ExamControllerDelegate --

	@Override
	public void examClearApps() {
		activeMaterial = null;
		didRequestClearApps = true;
	}

	public void examClearClipboard() {
		didRequestClearClipboard = true;
	}

	@Override
	public Material examGetActiveMaterial() {
		return activeMaterial;
	}

	@Override
	public void examSetActiveMaterial(Material material) {
		activeMaterial = material;
	}

	@Override
	public SuiteSubApp examGetCurrentSubApp() {
		return currentSubApp;
	}

	@Override
	public void examSwitchSubApp(SuiteSubApp subApp) {
		if (!subApp.equals(currentSubApp)) {
			switchApp(subApp);
		}
	}
}
