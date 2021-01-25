package org.geogebra.common.main.exam;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExamEventsTest extends BaseUnitTest {

	private ExamEnvironment exam;
	
	@Before
	public void setupExam() {
		exam = new ExamEnvironment(getApp().getLocalization());
	}

	@Test
	public void leavingAfterStartShouldTriggerRed() {
		exam.setStart(System.currentTimeMillis());
		exam.windowLeft();
		Assert.assertTrue(exam.isCheating());
	}

	@Test
	public void leavingBeforeStartShouldBeOK() {
		exam.windowLeft();
		exam.setStart(System.currentTimeMillis());
		Assert.assertFalse(exam.isCheating());
	}
}
