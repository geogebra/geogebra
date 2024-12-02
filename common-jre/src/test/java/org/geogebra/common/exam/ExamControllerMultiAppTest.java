package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.junit.Before;
import org.junit.Test;

public class ExamControllerMultiAppTest {

	private ExamController controller;
	private AppCommon app;

	@Before
	public void setup() {
		controller = new ExamController(new DefaultPropertiesRegistry(),
				GlobalScope.contextMenuFactory);
		app = AppCommonFactory.create3D();
	}

	@Test
	public void shouldNotifyAllDelegates() {
		TestExamDelegate firstDelegate = new TestExamDelegate();
		controller.registerContext(this,
				app.getKernel().getAlgebraProcessor().getCommandDispatcher(),
				app.getKernel().getAlgebraProcessor(),
				app.getLocalization(), app.getSettings(), null, null);
		controller.registerDelegate(firstDelegate);
		TestExamDelegate secondDelegate = new TestExamDelegate();
		controller.registerDelegate(secondDelegate);
		controller.startExam(ExamType.BAYERN_CAS, null);
		assertEquals(SuiteSubApp.CAS, firstDelegate.examGetCurrentSubApp());
		assertEquals(SuiteSubApp.CAS, secondDelegate.examGetCurrentSubApp());
		assertNotNull(firstDelegate.examGetActiveMaterial());
		assertNotNull(secondDelegate.examGetActiveMaterial());
		assertNotSame(firstDelegate.examGetActiveMaterial(),
				secondDelegate.examGetActiveMaterial());
		firstDelegate.examSetActiveMaterial(null);
		secondDelegate.examSetActiveMaterial(null);
		controller.createNewTempMaterial();
		assertNotNull(firstDelegate.examGetActiveMaterial());
		assertNotNull(secondDelegate.examGetActiveMaterial());
	}

}
