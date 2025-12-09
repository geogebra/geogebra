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

package org.geogebra.web.test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class DomMocker {

	/**
	 * @return element with mocked style
	 */
	public static GeoGebraElement getGeoGebraElement() {
		GeoGebraElement mock = mock(GeoGebraElement.class);

		when(mock.getStyle()).thenAnswer((Answer<Style>) invocation -> mock(Style.class));
		when(mock.getElement()).thenAnswer((Answer<Element>) invocation -> mock(Element.class));

		return mock;
	}

	/**
	 * @return element with consistent set/get behavior for attributes
	 */
	public static Element getElement() {
		Element element = mock(Element.class);
		final Map<String, String> attributes = new HashMap<>();
		Mockito.doAnswer((Answer<Void>) invocation -> {
			attributes.put(invocation.getArgumentAt(0, String.class),
					invocation.getArgumentAt(1, String.class));
			return null;
		}).when(element).setAttribute(Matchers.anyString(), Matchers.anyString());

		Mockito.doAnswer((Answer<Void>) invocation -> {
			attributes.put("innerText", invocation.getArgumentAt(0, String.class));
			return null;
		}).when(element).setInnerText(Matchers.anyString());

		when(element.getAttribute(Matchers.anyString()))
				.thenAnswer((Answer<String>) invocation ->
								attributes.get(invocation.getArgumentAt(0, String.class)));
		when(element.getInnerText())
				.thenAnswer((Answer<String>) invocation ->
						String.valueOf(attributes.get("innerText")));
		Style mockStyle = mock(Style.class);
		when(element.getStyle()).thenReturn(mockStyle);
		return element;
	}

	/**
	 * @param button widget
	 * @param <T> widget type
	 * @return widget with consistent backing element
	 */
	public static <T extends Widget> T withElement(T button) {
		T mock = spy(button);
		Element element = DomMocker.getElement();
		when(mock.getElement()).thenReturn(element);
		return mock;
	}

	/**
	 * @return label with consistent backing element
	 */
	public static Label newLabel() {
		Label lbl = withElement(new Label());
		bypassSetTextMethod(lbl);
		return lbl;
	}

	private static void bypassSetTextMethod(final Label lbl) {
		doAnswer((Answer<Void>) invocation -> {
			lbl.getElement().setInnerText(invocation.getArgumentAt(0, String.class));
			return null;
		}).when(lbl).setText(Matchers.anyString());
	}
}
