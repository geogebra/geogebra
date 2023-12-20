package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.exam.restriction.ExamRegion;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerTests implements ExamControllerDelegate {

	private AppCommon app;
	private ExamController examController;
	private List<ExamState> examStates = new ArrayList<>();

	@Before
	public void setUp() {
		app = AppCommonFactory.create();
		app.setConfig(new AppConfigNotes());

		examController = new ExamController(app.getKernel().getAlgebraProcessor().getCommandDispatcher());
		examController.setDelegate(this);
		examController.addListener(new ExamListener() {
			@Override
			public void examStateChanged(ExamState newState) {
				ExamControllerTests.this.examStates.add(newState);
			}
		});
		examStates.clear();
	}

	@Test
	public void testPrepareExam() {
		assertEquals(ExamState.INACTIVE, examController.getState());
		examController.prepareExam();
		assertNull(examController.getStartDate());
		assertNull(examController.getEndDate());
		assertEquals(ExamState.PREPARING, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING), examStates);
	}

	@Test
	public void testStartExam() {
		startExam(ExamRegion.VLAANDEREN);
		assertNotNull(examController.getStartDate());
		assertNull(examController.getEndDate());
		assertEquals(ExamState.ACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE), examStates);
	}

	@Test
	public void testStopExam() {
		startExam(ExamRegion.VLAANDEREN);
		examController.stopExam();
		assertNotNull(examController.getStartDate());
		assertNotNull(examController.getEndDate());
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

		assertEquals(ExamState.INACTIVE, examController.getState());
		assertEquals(Arrays.asList(ExamState.PREPARING, ExamState.ACTIVE,
				ExamState.WRAPPING_UP, ExamState.INACTIVE), examStates);
	}

	private void startExam(ExamRegion examRegion) {
		examController.prepareExam();
		ExamConfiguration configuration = new ExamConfiguration();
		examController.startExam(examRegion, configuration);
	}

	// -- ExamControllerDelegate --

	@Override
	public void clearAllApps() {
	}

	@Override
	public void clearClipboard() {
	}
}
