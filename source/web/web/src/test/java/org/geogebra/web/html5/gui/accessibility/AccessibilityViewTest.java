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

package org.geogebra.web.html5.gui.accessibility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Locale;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.DomMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.user.client.ui.Button;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({EuclidianSimplePanelW.class, JLMContext2D.class})
public class AccessibilityViewTest {

	private AppW app;
	private AccessibilityView aView;
	private ArrayFlowPanel mockPanel;
	private ArrayList<SliderW> sliders = new ArrayList<>();
	private BaseWidgetFactory baseWidgetFactory;

	@Before
	public void setup() {
		mockPanel = new ArrayFlowPanel();
		baseWidgetFactory = getBaseWidgetFactory();
	}

	private void initAccessibilityViewFull() {
		app = AppMocker.mockGraphing();
		aView = new AccessibilityView(app, baseWidgetFactory);
	}

	private void initAccessibilityViewSimple() {
		app = AppMocker.mockAppletSimple(new AppletParameters("simple"));
		aView = new AccessibilityView(app, baseWidgetFactory);
	}

	@Test
	public void pointsShouldBeSliders() {
		initAccessibilityViewFull();
		add("(1,1)");
		add("(1,2,3)");
		assertDescription(0, "x coordinate of point a");
		assertDescription(1, "y coordinate of point a");
		assertDescription(2, "x coordinate of point b");
		assertDescription(3, "y coordinate of point b");
		assertDescription(4, "z coordinate of point b");
	}

	@Test
	public void buttonsShouldReadCaption() {
		initAccessibilityViewFull();
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	@Test
	public void elementsShouldReadDescription() {
		initAccessibilityViewFull();
		add("\"legend\"");
		assertText(0, "legend");
	}

	@Test
	public void pointsShouldBeSlidersAppSimple() {
		initAccessibilityViewSimple();
		add("(1,1)");
		assertDescription(0, "x coordinate of point a");
		assertDescription(1, "y coordinate of point a");
	}

	@Test
	public void buttonsShouldReadCaptionAppSimple() {
		initAccessibilityViewSimple();
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	@Test
	public void elementsShouldReadDescriptionAppSimple() {
		initAccessibilityViewSimple();
		add("\"legend\"");
		assertText(0, "legend");
	}

	private void assertDescription(int i, String string) {
		assertTrue(mockPanel.getWidgetCount() > i);
		assertThat(
				mockPanel.getWidget(i).getElement().getAttribute("aria-label")
						.toLowerCase(Locale.ROOT),
				CoreMatchers.containsString(string));
	}

	private void assertText(int i, String string) {
		assertTrue(mockPanel.getWidgetCount() > i);
		assertThat(mockPanel.getWidget(i).getElement().getInnerText().toLowerCase(Locale.ROOT),
				CoreMatchers.containsString(string));
	}

	private GeoElementND add(String string) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(string, false)[0];
	}

	private BaseWidgetFactory getBaseWidgetFactory() {
		BaseWidgetFactory factory = mock(BaseWidgetFactory.class);
		when(factory.newButton()).thenAnswer(
				invocation -> DomMocker.withElement(new Button()));
		when(factory.newLabel()).thenAnswer(invocation -> DomMocker.newLabel());

		when(factory.newSlider(Matchers.anyInt(), Matchers.anyInt()))
				.thenAnswer(invocation -> DomMocker.withElement(new SliderW(0, 1)));
		when(factory.newPanel()).thenReturn(mockPanel);

		return factory;
	}
}
