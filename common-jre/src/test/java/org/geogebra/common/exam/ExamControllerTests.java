package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerTests implements ExamControllerDelegate {

	private ExamController examController;
	private PropertiesRegistry propertiesRegistry;
	private AppCommon app;
	private AppCommon previousApp;
	private CommandDispatcher commandDispatcher;
	private CommandDispatcher previousCommandDispatcher;
	private AlgebraProcessor algebraProcessor;
	private AlgebraProcessor previousAlgebraProcessor;
	private SuiteSubApp currentSubApp;
	private List<ExamState> examStates = new ArrayList<>();
	private boolean didRequestClearApps;
	private boolean didRequestClearClipboard;
	private SuiteSubApp didRequestSwitchToSubApp;
	private Material activeMaterial;

	@Before
	public void setUp() {
		app = null;
		algebraProcessor = null;
		commandDispatcher = null;

		propertiesRegistry = new DefaultPropertiesRegistry();

		examController = new ExamController(propertiesRegistry);
		examController.setDelegate(this);
		examController.addListener(newState -> {
			examStates.add(newState);
		});
		examStates.clear();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		didRequestSwitchToSubApp = null;
		activeMaterial = null;
	}

	// Helpers

	private void setInitialApp(SuiteSubApp subApp) {
		currentSubApp = subApp;
		app = AppCommonFactory.create(createConfig(subApp));
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		commandDispatcher = algebraProcessor.getCommandDispatcher();
		propertiesRegistry.register(new AngleUnitProperty(app.getKernel(), app.getLocalization(),
				propertiesRegistry), app);
		examController.setActiveContext(app, commandDispatcher, algebraProcessor);
	}

	private void switchApp(SuiteSubApp subApp) {
		// keep references so that we can check if restrictions have been reverted correctly
		previousApp = app;
		previousAlgebraProcessor = algebraProcessor;
		previousCommandDispatcher = commandDispatcher;

		currentSubApp = subApp;
		app = AppCommonFactory.create(createConfig(subApp));
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		commandDispatcher = algebraProcessor.getCommandDispatcher();
		propertiesRegistry.register(new AngleUnitProperty(app.getKernel(), app.getLocalization(),
				propertiesRegistry), app);
		examController.setActiveContext(app, commandDispatcher, algebraProcessor);
	}

	private AppConfig createConfig(SuiteSubApp subApp) {
		switch (subApp) {
		case CAS:
			return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
		case GRAPHING:
			return new AppConfigGraphing(GeoGebraConstants.SUITE_APPCODE);
		case GEOMETRY:
			return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
		case SCIENTIFIC:
//			switchApp(new AppConfigScientific(GeoGebraConstants.SUITE_APPCODE)); // ?
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
		assertEquals(Arrays.asList(ExamState.PREPARING), examStates);
		assertFalse(didRequestClearApps);
		assertFalse(didRequestClearClipboard);
		assertNull(didRequestSwitchToSubApp);
		assertNull(activeMaterial);
	}

	@Test
	public void testStartExam() {
		List<ExamState> listenerStates = new ArrayList<>();
		examController.addListener(newState -> listenerStates.add(newState));

		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);

		assertNotNull(examController.getStartDate()); // started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE), examStates);
		assertEquals(examStates, listenerStates);
		assertTrue(didRequestClearApps);
		assertTrue(didRequestClearClipboard);
		assertNull(didRequestSwitchToSubApp);
		assertNotNull(activeMaterial);
	}

	@Test
	public void testStartExamWithoutActiveContext() {
		examController.prepareExam();
		try {
			examController.startExam(ExamRegion.GENERIC);
			fail("starting exam without calling setActiveContext() should throw an IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	// start exam without calling prepare() first (e.g., in crash recovery)
	@Test
	public void testStartExamWithoutPrepare() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamRegion.VLAANDEREN);
		assertEquals(ExamState.ACTIVE, examController.getState());
	}

	@Test
	public void testFinishExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);
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
		examController.startExam(ExamRegion.VLAANDEREN);
		examController.finishExam();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		didRequestSwitchToSubApp = null;
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
		assertNull(didRequestSwitchToSubApp);
		assertNull(activeMaterial);
	}

	// Restrictions

	@Test
	public void testRestrictedSubApp() {
		setInitialApp(SuiteSubApp.CAS);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN); // doesn't allow CAS
		assertEquals(SuiteSubApp.GRAPHING, didRequestSwitchToSubApp);
	}

	@Test
	public void testNonRestrictedSubApp() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);
		assertNull(didRequestSwitchToSubApp);
	}

	@Test
	public void testRestrictions() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();

		examController.startExam(ExamRegion.VLAANDEREN);

		// command restrictions
		assertFalse(commandDispatcher.isAllowedByNameFilter(Commands.Derivative));

		// TODO commandArgumentFilters

		// expression restrictions
		assertNull(evaluate("true || false"));

		// property restrictions
		AngleUnitProperty angleUnit = (AngleUnitProperty) propertiesRegistry.lookup("AngleUnit");
		assertTrue(angleUnit.isFrozen());
		Integer angleUnitValue = angleUnit.getValue();
		angleUnit.setValue(angleUnitValue + 1);
		assertEquals(angleUnitValue, angleUnit.getValue());

		examController.finishExam();
		assertFalse(commandDispatcher.isAllowedByNameFilter(Commands.Derivative));
		examController.exitExam();
		assertTrue(commandDispatcher.isAllowedByNameFilter(Commands.Derivative));
	}

	@Test
	public void testRestrictionsWhenSwitchingApps() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);
		assertFalse(commandDispatcher.isAllowedByNameFilter(Commands.Derivative));

		switchApp(SuiteSubApp.GEOMETRY);
		// restrictions should be reverted on the previous (Graphing app) command dispatcher...
		assertTrue(previousCommandDispatcher.isAllowedByNameFilter(Commands.Derivative));
		// ...and applied to the new (Geometry app) command dispatcher
		assertFalse(commandDispatcher.isAllowedByNameFilter(Commands.Derivative));
	}

	// -- ExamControllerDelegate --

	@Override
	public void examClearOtherApps() {
		didRequestClearApps = true;
	}

	public void examClearClipboard() {
		didRequestClearClipboard = true;
	}

	@Override
	public void examCreateNewFile() {
		activeMaterial = null;
	}

	@Override
	public Material examGetActiveMaterial() {
		return activeMaterial;
	}

	@Override
	public void examSetActiveMaterial(Material material) {
		activeMaterial = material;
	}

	public SuiteSubApp examGetCurrentSubApp() {
		return currentSubApp;
	}

	public void examSwitchSubApp(SuiteSubApp subApp) {
		didRequestSwitchToSubApp = subApp;
		if (!subApp.equals(currentSubApp)) {
			switchApp(subApp);
		}
	}
}
