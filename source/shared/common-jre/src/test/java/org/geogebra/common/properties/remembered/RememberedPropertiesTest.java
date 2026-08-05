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

package org.geogebra.common.properties.remembered;

import static org.geogebra.common.properties.impl.objects.FontProperty.FontFamily;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.objects.FontProperty;
import org.geogebra.common.properties.impl.objects.NotesFontSizeProperty;
import org.geogebra.common.properties.remembered.handlers.RememberedFontFamilyHandler;
import org.geogebra.common.properties.remembered.handlers.RememberedFontSizeHandler;
import org.geogebra.test.BaseAppTestSetup;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RememberedPropertiesTest extends BaseAppTestSetup {
	private static final PropertyKey FONT_FAMILY_KEY = PropertyKey.of(FontProperty.class);
	private static final PropertyKey FONT_SIZE_KEY =
			PropertyKey.of(NotesFontSizeProperty.class);

	private RememberedProperties remembered;
	private final RememberedFontFamilyHandler fontFamilyHandler =
			new RememberedFontFamilyHandler();
	private final RememberedFontSizeHandler fontSizeHandler =
			new RememberedFontSizeHandler();

	@BeforeEach
	void setUp() {
		setupNotesApp();
		remembered = new RememberedProperties(List.of(
				fontFamilyHandler,
				fontSizeHandler
		));
	}

	@Test
	void applyWithNoRememberedPropertiesShouldSucceed() {
		InlineTextController controller = mock();

		assertTrue(remembered.apply(inlineText(controller)));
		verifyNoInteractions(controller);
	}

	@Test
	void shouldRememberAndApplyProperty() {
		InlineTextController controller = mock();
		remember(inlineText(), property(FONT_FAMILY_KEY, FontFamily.ARIAL));

		assertTrue(remembered.apply(inlineText(controller)));
		verify(controller).formatFont(FontFamily.ARIAL.cssName());
	}

	@Test
	void lastRememberedValueShouldWin() {
		remember(inlineText(), property(FONT_FAMILY_KEY, FontFamily.ARIAL));
		remember(inlineText(), property(FONT_FAMILY_KEY, FontFamily.CALIBRI));
		InlineTextController controller = mock();

		assertTrue(remembered.apply(inlineText(controller)));
		verify(controller).formatFont(FontFamily.CALIBRI.cssName());
		verifyNoMoreInteractions(controller);
	}

	@Test
	void fontFamilyAndSizeShouldBeSeparate() {
		GeoInlineText source = inlineText();
		remember(source, property(FONT_FAMILY_KEY, FontFamily.COURIER));
		remember(source, property(FONT_SIZE_KEY, "18"));
		InlineTextController controller = mock();

		assertTrue(remembered.apply(inlineText(controller)));
		verify(controller).formatFont(FontFamily.COURIER.cssName());
		verify(controller).format("size", 18.0);
	}

	@Test
	void shouldRememberValuesSeparatelyByGeoClass() {
		remember(inlineText(), property(FONT_FAMILY_KEY, FontFamily.ARIAL));
		remember(mindMapNode(), property(FONT_FAMILY_KEY, FontFamily.COMIC_SANS));
		InlineTextController inlineTextController = mock();
		InlineTextController mindMapController = mock();

		assertAll(
				() -> assertTrue(remembered.apply(inlineText(inlineTextController))),
				() -> assertTrue(remembered.apply(mindMapNode(mindMapController)))
		);
		verify(inlineTextController).formatFont(FontFamily.ARIAL.cssName());
		verify(mindMapController).formatFont(FontFamily.COMIC_SANS.cssName());
	}

	@Test
	void sameTypeMultiselectShouldBeRemembered() {
		remember(List.of(inlineText(), inlineText()),
				property(FONT_FAMILY_KEY, FontFamily.COURIER));
		InlineTextController controller = mock();

		assertTrue(remembered.apply(inlineText(controller)));
		verify(controller).formatFont(FontFamily.COURIER.cssName());
	}

	@Test
	void mixedTypeMultiselectShouldNotBeRemembered() {
		ValuedProperty<FontFamily> property =
				property(FONT_FAMILY_KEY, FontFamily.COMIC_SANS);

		remembered.observe(List.of(inlineText(), mindMapNode()), property);

		verify(property, never()).addValueObserver(any());
	}

	@Test
	void unsupportedGeoShouldNotBeRemembered() {
		GeoText source = new GeoText(getConstruction());
		ValuedProperty<FontFamily> property = property(FONT_FAMILY_KEY, FontFamily.TIMES);

		remembered.observe(List.of(source), property);

		verify(property, never()).addValueObserver(any());
		assertTrue(remembered.apply(new GeoText(getConstruction())));
	}

	@Test
	void applyShouldReturnFalseWhenHandlerFails() {
		remember(inlineText(), property(FONT_FAMILY_KEY, FontFamily.VERDANA));

		assertFalse(remembered.apply(inlineText()));
	}

	@Test
	void applyShouldAttemptAllHandlersAfterOneFails() {
		GeoInlineText source = inlineText();
		remember(source, property(FONT_FAMILY_KEY, FontFamily.TREBUCHET));
		remember(source, property(FONT_SIZE_KEY, "invalid"));
		InlineTextController controller = mock();

		assertFalse(remembered.apply(inlineText(controller)));
		verify(controller).formatFont(FontFamily.TREBUCHET.cssName());
	}

	@Test
	void unknownPropertyShouldNotBeObserved() {
		ValuedProperty<String> property = property(PropertyKey.of(ColorProperty.class), "red");

		remembered.observe(List.of(inlineText()), property);

		verify(property, never()).addValueObserver(any());
	}

	@Test
	void duplicatedHandlerRegistrationShouldFailFast() {
		assertThrows(UnsupportedOperationException.class,
				() -> new RememberedProperties(
						List.of(fontFamilyHandler, fontFamilyHandler)));
	}

	private GeoInlineText inlineText() {
		return new GeoInlineText(getConstruction(), null);
	}

	private GeoInlineText inlineText(InlineTextController controller) {
		GeoInlineText inlineText = new GeoInlineText(getConstruction(), new GPoint2D());
		return withController(inlineText, "inlineText", controller);
	}

	private GeoMindMapNode mindMapNode() {
		return new GeoMindMapNode(getConstruction(), null);
	}

	private GeoMindMapNode mindMapNode(InlineTextController controller) {
		GeoMindMapNode mindMapNode = new GeoMindMapNode(getConstruction(), new GPoint2D());
		return withController(mindMapNode, "mindMap", controller);
	}

	private <T extends GeoElement> T withController(T geo, String label,
			InlineTextController controller) {
		geo.setLabel(label);
		DrawableND drawable = getApp().getEuclidianView1().getDrawableFor(geo);
		DrawInlineText drawInlineText = assertInstanceOf(DrawInlineText.class, drawable);
		drawInlineText.setTextController(controller);
		return geo;
	}

	private <T> ValuedProperty<T> property(PropertyKey key, T value) {
		ValuedProperty<T> property = mock();
		when(property.getKey()).thenReturn(key);
		when(property.getValue()).thenReturn(value);
		return property;
	}

	private <T> void remember(@NonNull GeoElement geo, @NonNull ValuedProperty<T> property) {
		remember(List.of(geo), property);
	}

	private <T> void remember(@NonNull List<GeoElement> geos,
			@NonNull ValuedProperty<T> property) {
		doAnswer(invocation -> {
			PropertyValueObserver<T> observer = invocation.getArgument(0);
			observer.onDidSetValue(property);
			return null;
		}).when(property).addValueObserver(any());
		remembered.observe(geos, property);
	}
}
