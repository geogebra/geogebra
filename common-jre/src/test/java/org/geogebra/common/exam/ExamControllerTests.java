package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerTests implements ExamControllerDelegate {

	private AppCommon app;
	private AppCommon previouslyActiveApp;
	private CommandDispatcher commandDispatcher;
	private AlgebraProcessor algebraProcessor;
	private ExamController examController;
	private PropertiesRegistry propertiesRegistry;
	private List<ExamState> examStates = new ArrayList<>();
	private boolean didRequestClearApps = false;
	private boolean didRequestClearClipboard = false;
	private SuiteSubApp didRequestSwitchToApp = null;

	@Before
	public void setUp() {
		switchApp(SuiteSubApp.GRAPHING);

		propertiesRegistry = new DefaultPropertiesRegistry();
		examController = new ExamController(propertiesRegistry);

		examController.setActiveContext(app, commandDispatcher, algebraProcessor);
		examController.setDelegate(this);
		examController.addListener(newState -> {
			examStates.add(newState);
		});
		examStates.clear();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		didRequestSwitchToApp = null;
	}

	private void switchApp(SuiteSubApp subApp) {
		// keep a reference so that we can check if restrictions have been removed correctly
		// from the previously active app
		previouslyActiveApp = app;

		app = AppCommonFactory.create(createConfig(subApp));
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		commandDispatcher = algebraProcessor.getCommandDispatcher();
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

	// State & duration

	@Test
	public void testPrepareExam() {
		assertEquals(ExamState.IDLE, examController.getState());
		examController.prepareExam();

		assertNull(examController.getStartDate()); // not yet started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.PREPARING, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING), examStates);
	}

	@Test
	public void testStartExam() {
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);

		assertNotNull(examController.getStartDate()); // started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE), examStates);
		assertTrue(didRequestClearApps);
		assertTrue(didRequestClearClipboard);
	}

	@Test
	public void testFinishExam() {
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);
		examController.finishExam();

		assertNotNull(examController.getStartDate()); // started
		assertNotNull(examController.getFinishDate()); // ended
		assertEquals(ExamState.FINISHED, examController.getState());
		assertEquals(Arrays.asList(
				ExamState.PREPARING,
				ExamState.ACTIVE,
				ExamState.FINISHED), examStates);
	}

	@Test
	public void testExitExam() {
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);
		examController.finishExam();
		didRequestClearApps = false;
		didRequestClearClipboard = false;
		didRequestSwitchToApp = null;
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
	}

	// Restrictions

	@Test
	public void testSwitchToDefaultSubApp() {
		switchApp(SuiteSubApp.CAS);
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN); // doesn't allow CAS

		assertEquals(SuiteSubApp.GRAPHING, didRequestSwitchToApp);
	}

	@Test
	public void testApplyRestrictions() {
		examController.prepareExam();
		examController.startExam(ExamRegion.VLAANDEREN);

		assertNotNull(examController.getStartDate()); // started
		assertNull(examController.getFinishDate()); // not yet ended
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(
				ExamState.PREPARING,
				ExamState.ACTIVE), examStates);
	}

	// -- ExamControllerDelegate --

	@Override
	public void requestClearApps() {
		didRequestClearApps = true;
	}

	public void requestClearClipboard() {
		didRequestClearClipboard = true;
	}

	public void requestSwitchApp(SuiteSubApp subApp) {
		didRequestSwitchToApp = subApp;
		switchApp(subApp);
	}
}
