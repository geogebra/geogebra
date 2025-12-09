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
 
package org.geogebra.common.main;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.editor.share.util.KeyCodes;
import org.geogebra.test.EventAccumulator;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class GlobalKeyDispatcherTest extends BaseUnitTest {

	private GlobalKeyDispatcherHeadless dispatcher;

	@Before
	public void setupDispatcher() {
		this.dispatcher = new GlobalKeyDispatcherHeadless(getApp());
	}

	@Test
	public void moveWithArrowPoints() {
		GeoElement pt = add("(1,1)");
		GeoElement list = add("{(1,1)}");
		selectGeo(pt);
		handleKey(KeyCodes.UP, Arrays.asList(pt, list));
		assertThat(pt, hasValue("(1, 1.1)"));
		assertThat(list, hasValue("{(1, 1.1)}"));
		list.setFixed(true);
		handleKey(KeyCodes.DOWN, Arrays.asList(pt, list));
		assertThat(pt, hasValue("(1, 1)"));
		assertThat(list, hasValue("{(1, 1.1)}"));
	}

	private void selectGeo(GeoElement pt) {
		getApp().getSelectionManager().addSelectedGeo(pt);
	}

	@Test
	public void moveWithArrowRandom() {
		getApp().setRandomSeed(42);
		List<GeoElement> geos = Arrays.asList(add("num=random()"),
			add("pt=(random(),random())"),
			add("norm=RandomNormal(0,1)"),
			add("list=Shuffle(1..50)"));
		List<String> oldVals = geos.stream()
				.map(g -> g.toValueString(StringTemplate.defaultTemplate))
				.collect(Collectors.toList());
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		handleKey(KeyCodes.LEFT, geos);
		// each update fired exactly once
		assertThat(listener.getEvents(), is(Arrays.asList("UPDATE num",
				"UPDATE pt", "UPDATE norm", "UPDATE list")));
		// values actually changed
		for (int i = 0; i < 4; i++) {
			assertThat(geos.get(i).toValueString(StringTemplate.defaultTemplate),
					not(oldVals.get(i)));
		}
	}

	private void handleKey(KeyCodes keyCodes, List<GeoElement> selection) {
		dispatcher.handleSelectedGeosKeys(
				keyCodes, selection,
				false, false, false, false);
	}

	@Test
	public void handleSpaceOnIndependentBoolean() {
		GeoBoolean geoBoolean = add("a = true");
		geoBoolean.setEuclidianVisible(true);
		assertThat(geoBoolean.getBoolean(), is(true));
		selectGeo(geoBoolean);
		handleSpace();
		assertThat(geoBoolean.getBoolean(), is(false));
	}

	@Test
	public void handleSpaceOnSlider() {
		GeoNumeric slider = add("a = Slider(-5,5,1)");
		selectGeo(slider);
		handleSpace();
		assertThat(slider.isAnimating(), is(true));
		getApp().setRightClickEnabled(false);
		handleSpace();
		assertThat(slider.isAnimating(), is(true));
	}

	private void handleSpace() {
		dispatcher.handleGeneralKeys(
				KeyCodes.SPACE,
				false, false, false, false, false);
	}

	@Test
	public void handleSpaceOnDependentBoolean() {
		add("a = 42");
		GeoBoolean dependent = add("b = a > 100");
		selectGeo(dependent);
		handleSpace();
		assertThat(dependent.getBoolean(), is(false));
	}

	@Test
	public void handleSpaceOnHidingButton() {
		GeoButton button = add("btn=Button()");
		GeoNumeric counter = add("counter=1");
		GgbScript script = new GgbScript(getApp(), "SetVisibleInView(btn,1,false)"
				+ "\ncounter=counter+1");
		button.setClickScript(script);
		selectGeo(button);
		handleSpace();
		assertThat(counter.getValue(), is(2.0));
		// button should still be selected, but do nothing (APPS-5151, APPS-4792)
		assertThat(button, hasProperty("selected", GeoElement::isSelected, true));
		handleSpace();
		assertThat(counter.getValue(), is(2.0));
	}

	@Test
	public void ctrlZShouldCheckFlag() {
		getApp().setUndoRedoMode(UndoRedoMode.EXTERNAL);
		getKernel().setUndoActive(true);
		getApp().storeUndoInfo();
		add("a=0");
		getApp().storeUndoInfo();
		dispatcher.handleCtrlKeys(KeyCodes.Z, false, false,
				false);
		assertThat(lookup("a"), notNullValue());
		getApp().setUndoRedoMode(UndoRedoMode.GUI);
		dispatcher.handleCtrlKeys(KeyCodes.Z, false, false,
				false);
		assertThat(lookup("a"), nullValue());
	}

	@Test
	@Issue("APPS-6737")
	public void noSpreadsheetInstantiation() {
		GuiManager guiManager = mock(GuiManager.class);
		getApp().setGuiManager(guiManager);
		GeoPoint point = add("(1,1)");
		getApp().getSelectionManager().addSelectedGeo(point);
		dispatcher.handleSelectedGeosKeys(KeyCodes.BACKSPACE, List.of(point),
				false, false, false, false);
		assertEquals(0, getConstruction().getGeoSetConstructionOrder().size());
		verify(guiManager, never()).getSpreadsheetView();
	}

	@Test
	@Issue("APPS-7045")
	public void calculationShouldNotBeChangeableUsingArrowKeysOrPlusMinusKey() {
		GeoNumeric calculation = add("10 + 2");
		handleKey(KeyCodes.RIGHT, List.of(calculation));
		handleKey(KeyCodes.UP, List.of(calculation));
		handleKey(KeyCodes.PLUS, List.of(calculation));
		assertEquals(12, calculation.getValue(), 0);
		handleKey(KeyCodes.LEFT, List.of(calculation));
		handleKey(KeyCodes.DOWN, List.of(calculation));
		handleKey(KeyCodes.MINUS, List.of(calculation));
		assertEquals(12, calculation.getValue(), 0);
	}

	@Test
	@Issue("APPS-7045")
	public void calculationShouldNotBeChangeableWithMultipleElementsSelected() {
		GeoNumeric calculation = add("3 * 4");
		GeoNumeric numeric = add("2");
		GeoNumeric slider = add("Slider(-5,5,1)");
		handleKey(KeyCodes.RIGHT, List.of(calculation, numeric, slider));
		handleKey(KeyCodes.UP, List.of(calculation, numeric, slider));
		handleKey(KeyCodes.PLUS, List.of(calculation, numeric, slider));
		assertEquals(12, calculation.getValue(), 0);
		assertEquals(3.5, numeric.getValue(), 0);
		assertEquals(3, slider.getValue(), 0);
	}
}
