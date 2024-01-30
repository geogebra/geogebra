package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.kernel.commands.CommandDispatcherJre;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerTests implements ExamControllerDelegate {

	private AppCommon app;
	private CommandDispatcher commandDispatcher;
	private ExamController examController;
	private Object context = new Object();
	private PropertiesRegistry propertiesRegistry;
	private List<ExamState> examStates = new ArrayList<>();
	private List<ExamAction> actions = new ArrayList<>();

	@Before
	public void setUp() {
		app = AppCommonFactory.create();
		app.setConfig(new AppConfigNotes());
		commandDispatcher = new CommandDispatcherJre(app.getKernel());

		propertiesRegistry = new DefaultPropertiesRegistry();
		examController = new ExamController(propertiesRegistry,
				new ExamController.Dependencies(context,
						commandDispatcher,
						null,
						null));
		examController.setDelegate(this);
		examController.addListener(newState -> {
			examStates.add(newState);
		});
		examStates.clear();
		actions.clear();
	}

	@Test
	public void testPrepareExam() {
		assertEquals(ExamState.IDLE, examController.getState());
		examController.prepareExam();
		assertNull(examController.getStartDate()); // not yet started
		assertNull(examController.getEndDate()); // not yet ended
		assertEquals(ExamState.PREPARING, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING), examStates);
	}

	@Test
	public void testStartExam() {
		startExam(ExamRegion.VLAANDEREN);
		assertNotNull(examController.getStartDate()); // started
		assertNull(examController.getEndDate()); // not yet ended
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE), examStates);
		assertContains(ExamAction.CLEAR_APPS, actions);
		assertContains(ExamAction.CLEAR_CLIPBOARD, actions);
	}

	@Test
	public void testStopExam() {
		startExam(ExamRegion.VLAANDEREN);
		examController.stopExam();
		assertNotNull(examController.getStartDate()); // started
		assertNotNull(examController.getEndDate()); // ended
		assertEquals(ExamState.WRAPPING_UP, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE,
				ExamState.WRAPPING_UP), examStates);
	}

	@Test
	public void testFinishExam() {
		startExam(ExamRegion.VLAANDEREN);
		examController.stopExam();
		examController.finishExam();
		assertNull(examController.getStartDate());
		assertNull(examController.getEndDate());

		assertEquals(ExamState.IDLE, examController.getState()); // back to initial state
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE,
				ExamState.WRAPPING_UP, ExamState.IDLE), examStates);
	}

	private void startExam(ExamRegion examType) {
		examController.prepareExam();
		examController.startExam(examType);
	}

	private <T> void assertContains(T value, Collection<T> collection) {
		assertTrue(collection.contains(value));
	}

	// -- ExamControllerDelegate --

	@Override
	public void requestAction(ExamAction action) {
		actions.add(action);
	}
}
