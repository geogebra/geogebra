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

package org.geogebra.common.contextmenu.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.scientific.LabelController;
import org.junit.Before;
import org.junit.Test;

public class CreateSliderTest extends BaseUnitTest {

	CreateSlider createSlider;

	@Before
	public void setupItem() {
		createSlider = new CreateSlider(getAlgebraProcessor(), new LabelController());
	}

	@Test
	public void shouldBeAvailableForPlainNumbers() {
		assertThat(createSlider.isAvailable(add("a=4")), equalTo(true));
	}

	@Test
	public void shouldNotBeAvailableForComputations() {
		assertThat(createSlider.isAvailable(add("1+1")), equalTo(false));
		assertThat(createSlider.isAvailable(add("Mod(4,2)")), equalTo(false));
		assertThat(createSlider.isAvailable(add("inf")), equalTo(false));
	}

	@Test
	public void shouldSetAllProperties() {
		GeoNumeric number = add("4");
		createSlider.execute(number);
		assertThat(number.getIntervalMin(), equalTo(-5.0));
		assertThat(number.getIntervalMax(), equalTo(5.0));
		assertThat(number.isAVSliderOrCheckboxVisible(), equalTo(true));
	}

	@Test
	public void shouldShowAsDecimal() {
		GeoNumeric number = add(".5");
		number.setSymbolicMode(true, true);
		createSlider.execute(number);
		assertEquals("a = 0.5", number.toString(StringTemplate.testTemplate));
	}
}
