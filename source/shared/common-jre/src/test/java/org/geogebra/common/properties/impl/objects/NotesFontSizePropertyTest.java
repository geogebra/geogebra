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

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawStickyNote;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoStickyNote;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.facade.StringPropertyWithSuggestionsListFacade;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Tests the sticky-note font sizing mode exposed through the Notes font-size property. */
@Issue("MOW-1944")
class NotesFontSizePropertyTest extends BaseAppTestSetup {
	private GeoStickyNote note;
	private InlineTextController controller;
	private NotesFontSizeProperty property;

	@BeforeEach
	void setup() {
		setupNotesApp();
		note = new GeoStickyNote(getConstruction(), new GPoint2D(0, 0));
		note.setLabel("note");
		controller = mock(InlineTextController.class);
		when(controller.fitsAtFontSize(anyDouble())).thenReturn(true);
		DrawStickyNote drawable =
				(DrawStickyNote) getApp().getActiveEuclidianView().getDrawableFor(note);
		drawable.setTextController(controller);
		when(controller.getFormat("size", (Number) 16)).thenReturn(30.0);
		property = createProperty(note);
	}

	@Test
	void stickyNoteDefaultsToAuto() {
		assertTrue(note.isAutoFontSize());
		assertEquals("Auto", property.getValue());
	}

	@Test
	void fixedSizeAndAutoCanBeSelectedInBothDirections() {
		property.setValue("30");
		assertFalse(note.isAutoFontSize());
		assertEquals("30", property.getValue());
		verify(controller).format("size", 30.0);

		property.setValue("Auto");
		assertTrue(note.isAutoFontSize());
		assertEquals("Auto", property.getValue());
		// Selecting Auto must not pass a sentinel or another numeric size to the formatter.
		verify(controller).format(eq("size"), any());
	}

	@Test
	void autoModeSurvivesPropertyRecreation() {
		property.setValue("30");
		assertEquals("30", createProperty(note).getValue());
		property.setValue("Auto");
		assertEquals("Auto", createProperty(note).getValue());
	}

	@ParameterizedTest
	@CsvSource({"25", "50", "100"})
	void fixedSizeIncludesResizeScaleButNotViewZoom(int viewScale) {
		when(controller.getFormat("size", (Number) 16)).thenReturn(20.0);
		note.setAutoFontSize(false);
		note.setScale(1.5);
		getApp().getActiveEuclidianView().setCoordSystem(250, 250, viewScale, viewScale);
		note.zoomIfNeeded();

		assertEquals("30", property.getValue());
		verify(controller, never()).format(eq("size"), any());
	}

	@ParameterizedTest
	@CsvSource({"0.75, 15", "1.225, 25"})
	void scaledFixedSizeIsRounded(double scale, String expected) {
		when(controller.getFormat("size", (Number) 16)).thenReturn(20.0);
		note.setAutoFontSize(false);
		note.setScale(scale);
		assertEquals(expected, property.getValue());
	}

	@Test
	void committingFixedSizeAccountsForExistingScale() {
		note.setScale(1.5);
		property.setValue("24");
		verify(controller).format("size", 16.0);
		when(controller.getFormat("size", (Number) 16)).thenReturn(16.0);
		assertEquals("24", property.getValue());
	}

	@Test
	void autoLabelIsUnaffectedByResizeScale() {
		note.setScale(1.5);
		assertEquals("Auto", property.getValue());
	}

	@Test
	void validationDoesNotChangeAutoMode() {
		assertNull(property.validateValue("30"));
		assertNotNull(property.validateValue("invalid"));
		assertTrue(note.isAutoFontSize());
		assertEquals("Auto", property.getValue());
		verify(controller, never()).format(eq("size"), any());
	}

	@Test
	void validatingAutoDoesNotCommitIt() {
		note.setAutoFontSize(false);
		assertNull(property.validateValue("Auto"));
		assertFalse(note.isAutoFontSize());
		assertEquals("30", property.getValue());
	}

	@Test
	void invalidValueDoesNotChangeModeOrFormat() {
		property.setValue("invalid");
		assertTrue(note.isAutoFontSize());
		verify(controller, never()).format(eq("size"), any());
	}

	@Test
	void ordinaryInlineTextDoesNotAcceptAuto() {
		GeoInlineText text = new GeoInlineText(getConstruction(), new GPoint2D(0, 0));
		text.setLabel("text");
		NotesFontSizeProperty textProperty = createProperty(text);
		assertNotNull(textProperty.validateValue("Auto"));
		assertFalse(textProperty.getSuggestions().contains("Auto"));
		assertEquals("16", textProperty.getValue());
		textProperty.setValue("Auto");
		assertEquals("16", textProperty.getValue());
	}

	@Test
	void autoIsPrependedOnlyForStickyNotes() {
		assertEquals(
				List.of("Auto", "12", "16", "24", "36", "48", "60", "72", "96", "120"),
				property.getSuggestions());
	}

	@ParameterizedTest
	@CsvSource({"Automatisch, Auto", "XYZ, XYZ", "自動, 自動"})
	void automaticLabelUsesTranslationAndHandlesShortLabels(String translation, String label) {
		Localization localization = mock(Localization.class);
		when(localization.getMenu("Automatic")).thenReturn(translation);
		NotesFontSizeProperty localized =
				assertDoesNotThrow(() -> new NotesFontSizeProperty(localization, note));
		assertEquals(label, localized.getValue());
		assertEquals(label, localized.getSuggestions().get(0));
		assertNull(localized.validateValue(label));
	}

	@Test
	void numericParserRemainsIndependentOfAutoLabel() {
		assertEquals(30, NotesFontSizeProperty.parse("30"));
		assertNull(NotesFontSizeProperty.parse("Auto"));
	}

	@Test
	void overflowingFixedSizesAreDisabledAndRejectedWithoutChangingMode() {
		when(controller.fitsAtFontSize(120)).thenReturn(false);
		assertFalse(property.isSuggestionEnabled("120"));
		assertTrue(property.isSuggestionEnabled("96"));
		assertNotNull(property.validateValue("120"));
		property.setValue("120");
		assertTrue(note.isAutoFontSize());
		verify(controller, never()).format(eq("size"), any());
	}

	@Test
	void typedNonPresetSizesAreAlsoCheckedForOverflow() {
		when(controller.fitsAtFontSize(101)).thenReturn(false);
		assertNotNull(property.validateValue("101"));
		property.setValue("101");
		verify(controller, never()).format(eq("size"), any());
	}

	@Test
	void autoRemainsEnabledEvenWhenNoFixedSizeFits() {
		when(controller.fitsAtFontSize(anyDouble())).thenReturn(false);
		assertTrue(property.isSuggestionEnabled("Auto"));
		assertNull(property.validateValue("Auto"));
		assertTrue(property.getSuggestions().stream().skip(1).noneMatch(property::isSuggestionEnabled));
	}

	@Test
	void fitChecksUseLayoutSizeAndIgnoreViewZoom() {
		note.setScale(1.5);
		getApp().getActiveEuclidianView().setCoordSystem(250, 250, 100, 100);
		when(controller.fitsAtFontSize(16)).thenReturn(false);
		assertFalse(property.isSuggestionEnabled("24"));
		verify(controller).fitsAtFontSize(16);
	}

	@Test
	void disabledSuggestionsRefreshAfterTextChanges() {
		when(controller.fitsAtFontSize(96)).thenReturn(false, true);
		assertFalse(property.isSuggestionEnabled("96"));
		assertTrue(property.isSuggestionEnabled("96"));
	}

	@Test
	void multiSelectionRequiresTheSizeToFitEveryNote() {
		GeoStickyNote second = new GeoStickyNote(getConstruction(), new GPoint2D(1, 1));
		second.setLabel("second");
		InlineTextController otherController = mock(InlineTextController.class);
		((DrawStickyNote) getApp().getActiveEuclidianView().getDrawableFor(second))
				.setTextController(otherController);
		when(otherController.fitsAtFontSize(96)).thenReturn(false);
		StringPropertyWithSuggestionsListFacade<NotesFontSizeProperty> facade =
				new StringPropertyWithSuggestionsListFacade<>(List.of(property, createProperty(second)));
		assertFalse(facade.isSuggestionEnabled("96"));
		assertNotNull(facade.validateValue("96"));
		assertTrue(facade.isSuggestionEnabled("Auto"));
	}

	private @NonNull NotesFontSizeProperty createProperty(@NonNull GeoElement element) {
		return assertDoesNotThrow(() -> new NotesFontSizeProperty(getLocalization(), element));
	}
}
