package org.geogebra.web.html5.gui.accessibility;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
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
		BaseWidgetFactory factory = mock(BaseWidgetFactory.class);
		when(factory.newButton()).thenAnswer(new Answer<Button>() {

			@Override
			public Button answer(InvocationOnMock invocation) throws Throwable {
				return DomMocker.withElement(new Button());
			}
		});
		when(factory.newLabel()).thenAnswer(new Answer<Label>() {

			@Override
			public Label answer(InvocationOnMock invocation) throws Throwable {
				return DomMocker.newLabel();
			}
		});
		
		when(factory.newSlider(Matchers.anyInt(), Matchers.anyInt()))
				.thenAnswer(new Answer<SliderW>() {

					@Override
					public SliderW answer(InvocationOnMock invocation)
							throws Throwable {
						return DomMocker.withElement(new SliderW(0, 1));
					}
				});
		when(factory.newPanel()).thenReturn(mockPanel);
		aView = new AccessibilityView(app, factory);
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
	public void buttonsShouldReadCaption() {
		GeoElement button = add("Button(\"Click Me\")").toGeoElement();
		button.remove();
		button.setClickScript(new GgbScript(app, "42"));
		button.setLabel("B");
		assertText(0, "click me");
	}

	@Test
	public void elementsShouldReadDescription() {
		add("\"legend\"");
		assertText(0, "legend");
	}

	private void assertDescription(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(
				mockPanel.getWidget(i).getElement().getAttribute("aria-label").toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private void assertText(int i, String string) {
		Assert.assertTrue(mockPanel.getWidgetCount() > i);
		MatcherAssert.assertThat(mockPanel.getWidget(i).getElement().getInnerText().toLowerCase(),
				CoreMatchers.containsString(string));
	}

	private GeoElementND add(String string) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(string, false)[0];
	}

}
