package org.geogebra.web.html5.gui.accessibility;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.DomMocker;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AccessibilityViewTest {

	private AppW app;
	private AccessibilityView aView;
	private ArrayFlowPanel mockPanel;
	private ArrayList<SliderW> sliders = new ArrayList<>();

	@Before
	public void setup() {
		app = AppMocker.mockGraphing(AccessibilityViewTest.class);

		mockPanel = new ArrayFlowPanel();
		WidgetFactory factory = mock(WidgetFactory.class);
		Button mockButton = spy(new Button());
		Element element = DomMocker.getElement();
		when(mockButton.getElement()).thenReturn(element);
		when(factory.newButton()).thenReturn(mockButton);
		when(factory.newPanel()).thenReturn(mockPanel);
		when(factory.makeSlider(Matchers.anyInt(), Matchers.<HasSliders> any()))
				.thenAnswer(new Answer<SliderW>() {

					@Override
					public SliderW answer(InvocationOnMock invocation) throws Throwable {
						return mockSlider();
					}
				});
		aView = new AccessibilityView(app, factory);
	}

	protected static SliderW mockSlider() {
		SliderW slider = spy(new SliderW(0, 1));
		Element element = DomMocker.getElement();
		when(slider.getElement()).thenReturn(element);
		return slider;
	}

	@Test
	public void pointsShouldBeSliders() {
		add("(1,1)");
		add("(1,2,3)");
		assertDescription(0, "x coordinate of point a");
		assertDescription(1, "y coordinate of point a");
		assertDescription(2, "x coordinate of point b");
		assertDescription(3, "y coordinate of point b");
		assertDescription(4, "z coordinate of point b");
	}

	@Test
	public void buttonsShouldBeClickable() {
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	private void assertDescription(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(
				mockPanel.getWidget(i).getElement().getAttribute("aria-label").toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private void assertText(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(
				mockPanel.getWidget(i).getClass()
						+ mockPanel.getWidget(i).getElement().getInnerText().toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private GeoElementND add(String string) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(string, false)[0];
	}

}
