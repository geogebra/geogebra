package org.geogebra.common.main.exam;

import org.geogebra.common.main.AppCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExamEventsTest {

	private ExamEnvironment exam;
	
	@Before
	public void setup() {
		AppCommon app = new AppCommon();
		exam = new ExamEnvironment(app);
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
