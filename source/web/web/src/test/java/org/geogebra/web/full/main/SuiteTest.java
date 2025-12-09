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

package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.euclidian.quickstylebar.QuickStyleBar;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class SuiteTest {
	private AppWFull app;

	@Test
	public void examMode() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		GlobalHeader.INSTANCE.setApp(app);
		app.startExam(ExamType.GENERIC, null);
		app.switchToSubapp(SuiteSubApp.GEOMETRY);
		GlobalScope.examController.finishExam();
		app.endExam();
		assertTrue(app.getSettings().getCasSettings().isEnabled());
	}

	@Test
	public void filterTest() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		algebraProcessor.processAlgebraCommand("h(x)=x", false);
		algebraProcessor.processAlgebraCommand("l={1}*2", false);
		assertThat(getValueString("h"), equalTo("h(x) = x"));
		assertThat(getValueString("l"), equalTo("l = {2}"));
		app.switchToSubapp(SuiteSubApp.SCIENTIFIC);
		algebraProcessor.processAlgebraCommand("h(x)=x", false);
		algebraProcessor.processAlgebraCommand("l={1}*2", false);
		assertThat(app.getKernel().lookupLabel("h"), nullValue());
		assertThat(app.getKernel().lookupLabel("l"), nullValue());
	}

	@Test
	public void openGraphingFileFromProbCalc() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		app.startExam(ExamType.GENERIC, null);
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		algebraProcessor.processAlgebraCommand("gg:Circle((1,2),(3,4))", false);
		String cons = app.getGgbApi().getXML();
		app.switchToSubapp(SuiteSubApp.PROBABILITY);
		app.setXML(cons, true);
		assertTrue(app.getKernel().lookupLabel("gg").getParentAlgorithm()
				instanceof AlgoCircleTwoPoints);
	}

	@Test
	public void styleBarTest() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		QuickStyleBar styleBar = new QuickStyleBar(app.getActiveEuclidianView(), app) {
			@Override
			public boolean isVisible() {
				return true; // force updateStyleBar to do something
			}
		};
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		algebraProcessor.processAlgebraCommand("h(x)=x", false);
		app.getSelectionManager().addSelectedGeo(app.getKernel().lookupLabel("h"));
		styleBar.updateStyleBar();
		assertEquals(5, styleBar.getWidgetCount());
	}

	@Test
	public void tableOfValues() {
		app = AppMocker.mockApplet(new AppletParameters("suite"));
		app.switchToSubapp(SuiteSubApp.SCIENTIFIC);
		app.switchToSubapp(SuiteSubApp.CAS);
		AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
		GeoElementND[] geos = algebraProcessor.processAlgebraCommand("x", false);
		new LabelController().hideLabel(geos[0]);
		app.getGuiManager().addGeoToTableValuesView(geos[0].toGeoElement());
		assertEquals("f", geos[0].getLabelSimple());
		assertTrue("CAS should allow adding columns",
				app.getGuiManager().getTableValuesViewOrNull()
						.getTableValuesModel().allowsAddingColumns());
	}

	private String getValueString(String label) {
		return app.getKernel().lookupLabel(label).toString(StringTemplate.testTemplate);
	}
}
