package org.geogebra.web.test;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.user.client.Element;

public class DomMocker {
	public static Element getElement() {
		Element elementWithTitle = Mockito.mock(Element.class);
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
		Mockito.when(elementWithTitle.getInnerText())
				.thenAnswer(new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						return title.get("innerText");
					}
				});
		return elementWithTitle;
	}
}
