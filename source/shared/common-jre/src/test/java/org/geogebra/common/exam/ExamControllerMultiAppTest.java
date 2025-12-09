/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
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
				GlobalScope.geoElementPropertiesFactory,
				GlobalScope.contextMenuFactory);
		app = AppCommonFactory.create3D();
	}

	@Test
	public void shouldNotifyAllDelegates() {
		TestExamDelegate firstDelegate = new TestExamDelegate();
		controller.registerContext(new ExamController.ContextDependencies(this,
				app.getKernel().getAlgoDispatcher(),
				app.getKernel().getAlgebraProcessor().getCommandDispatcher(),
				app.getKernel().getAlgebraProcessor(),
				app.getLocalization(), app.getSettings(), new StatisticGroupsBuilder(),
				null, app, null, null));

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
