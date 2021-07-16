package org.geogebra.web.test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.util.GeoGebraElement;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DomMocker {

	public static GeoGebraElement getGeoGebraElement() {
		GeoGebraElement mock = mock(GeoGebraElement.class);

		when(mock.getStyle()).thenAnswer(new Answer<Style>() {
			@Override
			public Style answer(InvocationOnMock invocation) throws Throwable {
				return mock(Style.class);
			}
		});

		return mock;
	}

	public static Element getElement() {
		Element elementWithTitle = mock(Element.class);
		final Map<String, String> title = new HashMap<>();
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				title.put(invocation.getArgumentAt(0, String.class),
						invocation.getArgumentAt(1, String.class));
				return null;
			}
		}).when(elementWithTitle).setAttribute(Matchers.anyString(), Matchers.anyString());
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				title.put("innerText", invocation.getArgumentAt(0, String.class));
				return null;
			}
		}).when(elementWithTitle).setInnerText(Matchers.anyString());
		Mockito.when(elementWithTitle.getAttribute(Matchers.anyString()))
				.thenAnswer(new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						return title.get(invocation.getArgumentAt(0, String.class));
					}
				});
		when(elementWithTitle.getInnerText())
				.thenAnswer(new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						return String.valueOf(title.get("innerText"));
					}
				});
		Style mockStyle = mock(Style.class);
		when(elementWithTitle.getStyle()).thenReturn(mockStyle);
		return elementWithTitle;
	}

	public static <T extends Widget> T withElement(T button) {
		T mock = spy(button);
		Element element = DomMocker.getElement();
		when(mock.getElement()).thenReturn(element);
		return mock;
	}

	public static Label newLabel() {
		Label lbl = withElement(new Label());
		bypassSetTextMethod(lbl);
		return lbl;
	}

	private static void bypassSetTextMethod(final Label lbl) {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				lbl.getElement().setInnerText(invocation.getArgumentAt(0, String.class));
				return null;
			}
		}).when(lbl).setText(Matchers.anyString());
	}
}
