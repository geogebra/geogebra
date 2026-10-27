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

package org.geogebra.common.gui.colorpicker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.ObjectColorProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.common.spreadsheet.core.CellBackgroundColorProperty;
import org.geogebra.common.spreadsheet.core.CellTextColorProperty;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ColorPickerModelTest extends BaseAppTestSetup {
	private static final GColor C1 = GColor.newColorRGB(0x670000);
	private static final GColor C2 = GColor.newColorRGB(0x9E4848);
	private static final GColor C3 = GColor.newColorRGB(0x1565C0);
	private static final double EPS = 1e-9;

	private final ColorHistory history = new ColorHistory();
	private GColor originalColor;
	private ColorPickerModel model;

	@BeforeEach
	void setup() throws Exception {
		setupApp(SuiteSubApp.GRAPHING);
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		GeoElement point = evaluateGeoElement("A = (1, 2)");
		getApp().storeUndoInfo();
		originalColor = point.getObjectColor();
		ObjectColorProperty property = new ObjectColorProperty(getLocalization(), point);
		property.addValueObserver(new UndoSavingPropertyObserver(getApp().getUndoManager()));
		model = new ColorPickerModel(property, history);
	}

	private GColor currentColor() {
		return model.getColor().get().toGColor();
	}

	private GColor colorOfA() {
		return lookup("A").getObjectColor();
	}

	@Test
	void initialStateReflectsProperty() {
		assertEquals(originalColor, currentColor());
		assertEquals(List.of(originalColor), model.getYourColors().get());
		assertFalse(history.hasLiveSlot());
		assertFalse(model.getEditorExpanded().get());
	}

	@Test
	void selectColorUpdatesObjectAndLiveSlot() {
		model.selectColor(C1);
		model.selectColor(C2);
		assertEquals(C2, colorOfA());
		assertTrue(history.hasLiveSlot());
		assertEquals(List.of(C2, originalColor), model.getYourColors().get());
		model.finish();
		assertFalse(history.hasLiveSlot());
	}

	@Test
	void sessionCreatesOneUndoPoint() {
		model.setHue(30);
		model.setSaturationValue(0.7, 0.4);
		model.selectColor(C1);
		model.finish();
		assertEquals(C1, colorOfA());
		getKernel().undo();
		assertEquals(originalColor, colorOfA());
	}

	@Test
	void unchangedRgbStartsNoSession() {
		int historySize = getApp().getUndoManager().getHistorySize();
		model.selectColor(originalColor);
		model.setHue(model.getColor().get().getHue() + 1e-6); // same RGB after rounding
		assertFalse(history.hasLiveSlot());
		model.finish();
		assertEquals(historySize, getApp().getUndoManager().getHistorySize());
	}

	@Test
	void hueIsKeptWhenDraggingToBlack() {
		model.setHue(200);
		model.setSaturationValue(0.5, 0);
		assertEquals(200, model.getColor().get().getHue(), EPS);
		assertEquals(0.5, model.getColor().get().getSaturation(), EPS);
		assertEquals(GColor.BLACK, colorOfA());
	}

	@Test
	void commitHexIgnoresWhitespaceAndMissingHash() {
		model.commitHex(" 670000 ");
		assertEquals(C1, colorOfA());
		model.commitHex("#9e4848");
		assertEquals(C2, colorOfA());
	}

	@Test
	void commitRgbChangesSingleChannel() {
		model.selectColor(C1);
		model.commitRgb(ColorPickerModel.Channel.GREEN, "16");
		assertEquals(GColor.newColorRGB(0x671000), colorOfA());
	}

	@Test
	void invalidTextInputIsIgnored() {
		model.commitHex("");
		model.commitHex("#12");
		model.commitRgb(ColorPickerModel.Channel.RED, "256");
		assertEquals(originalColor, colorOfA());
		assertFalse(history.hasLiveSlot());
	}

	@Test
	void unchangedCommitsKeepHandlePositions() {
		model.setHue(200);
		model.setSaturationValue(0.3, 0.02); // renders as #040505, which reads back as 180°, 20%
		HsvColor before = model.getColor().get();
		model.commitHex(before.toHex());
		model.commitRgb(ColorPickerModel.Channel.RED, String.valueOf(currentColor().getRed()));
		model.selectColor(currentColor());
		model.selectYourColor(currentColor());
		assertEquals(before, model.getColor().get());
	}

	@Test
	void selectYourColorKeepsHistoryAndLiveSlotUnchanged() {
		history.add(C1);
		history.add(C2);
		model.selectYourColor(C1);
		assertEquals(C1, colorOfA());
		assertFalse(history.hasLiveSlot());
		assertEquals(List.of(C2, C1, originalColor), history.getColors().get());
		model.selectColor(C3);
		model.selectYourColor(C1);
		assertEquals(C1, colorOfA());
		assertEquals(List.of(C3, C2, C1, originalColor), history.getColors().get());
		model.finish();
		getKernel().undo();
		assertEquals(originalColor, colorOfA());
	}

	@Test
	void expandEditor() {
		model.expandEditor();
		assertTrue(model.getEditorExpanded().get());
	}

	@Test
	void finishIsIdempotentAndIgnoresLaterIntents() {
		model.selectColor(C1);
		model.finish();
		assertDoesNotThrow(model::finish);
		model.selectColor(C2);
		assertEquals(C1, colorOfA());
	}

	@Test
	void mixedMultiSelectionAppliesFirstPickEvenIfItMatchesFirstElement() {
		GeoElement b = evaluateGeoElement("B = (3, 4)");
		b.setObjColor(C2);
		getApp().storeUndoInfo();
		ColorProperty property = suiteScope.geoElementPropertiesFactory.createObjectColorProperty(
				getLocalization(), List.of(lookup("A"), b), DefaultColorValues.BRIGHT);
		assertNotNull(property);
		property.addValueObserver(new UndoSavingPropertyObserver(getApp().getUndoManager()));
		ColorPickerModel multi = new ColorPickerModel(property, history);
		multi.selectColor(originalColor);
		assertEquals(originalColor, lookup("B").getObjectColor());
		multi.finish();
		getKernel().undo();
		assertEquals(originalColor, colorOfA());
		assertEquals(C2, lookup("B").getObjectColor());
	}

	@Test
	void nullPropertyValueStartsFromBlackWithoutHistoryEntry() throws Exception {
		GeoElement text = evaluateGeoElement("t = \"hello\"");
		TextBackgroundColorProperty background =
				new TextBackgroundColorProperty(getLocalization(), text);
		assertNull(background.getValue());
		ColorPickerModel picker = new ColorPickerModel(background, history);
		assertEquals(GColor.BLACK, picker.getColor().get().toGColor());
		assertEquals(List.of(originalColor), history.getColors().get());
	}

	@Test
	void selectingBlackAppliesToUnsetBackgroundAndStartsSession() throws Exception {
		GeoElement text = evaluateGeoElement("t = \"hello\"");
		getApp().storeUndoInfo();
		TextBackgroundColorProperty background =
				new TextBackgroundColorProperty(getLocalization(), text);
		background.addValueObserver(new UndoSavingPropertyObserver(getApp().getUndoManager()));
		assertNull(background.getValue());
		ColorPickerModel picker = new ColorPickerModel(background, history);

		picker.selectColor(GColor.BLACK);

		assertEquals(GColor.BLACK, background.getValue());
		assertTrue(history.hasLiveSlot());
		assertEquals(List.of(GColor.BLACK, originalColor), history.getColors().get());
		picker.finish();
		assertFalse(history.hasLiveSlot());
		getKernel().undo();
		assertNull(lookup("t").getBackgroundColor());
	}

	@ParameterizedTest
	@CsvSource({"true, true", "false, true", "true, false", "false, false"})
	void spreadsheetSelectionAppliesRepresentativeColorOnlyWhenNeeded(
			boolean background, boolean multiCell) {
		SpreadsheetStyling styling = new SpreadsheetStyling();
		List<TabularRange> first = List.of(new TabularRange(0, 0));
		List<TabularRange> second = List.of(new TabularRange(0, 1));
		List<TabularRange> selection = multiCell ? List.of(new TabularRange(0, 0, 0, 1)) : first;
		ColorProperty property;
		if (background) {
			styling.setBackgroundColor(GColor.RED, first);
			styling.setBackgroundColor(GColor.BLUE, second);
			property = new CellBackgroundColorProperty(styling, selection, getLocalization());
		} else {
			styling.setTextColor(GColor.RED, first);
			styling.setTextColor(GColor.BLUE, second);
			property = new CellTextColorProperty(styling, selection, getLocalization());
		}
		assertEquals(GColor.RED, property.getValue());
		ColorHistory cellHistory = new ColorHistory();
		ColorPickerModel picker = new ColorPickerModel(property, cellHistory);

		picker.selectColor(GColor.RED);

		for (int column = 0; column < (multiCell ? 2 : 1); column++) {
			assertEquals(
					GColor.RED,
					background
							? styling.getBackgroundColor(0, column, GColor.WHITE)
							: styling.getTextColor(0, column, GColor.BLACK));
		}
		assertEquals(multiCell, cellHistory.hasLiveSlot());
		picker.finish();
		assertFalse(cellHistory.hasLiveSlot());
	}
}
