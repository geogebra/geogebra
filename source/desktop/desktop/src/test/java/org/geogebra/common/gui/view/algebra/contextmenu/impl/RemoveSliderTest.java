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

package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.editor.share.util.Unicode;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

public class RemoveSliderTest extends BaseSymbolicTest {

	private RemoveSlider removeSlider;
	private CreateSlider createSlider;

	@Before
	public void setUp() {
		removeSlider = new RemoveSlider(ap);

		LabelController controller = new LabelController();
		createSlider = new CreateSlider(ap, controller);
	}

	@Test
	public void testExecute() {
		GeoElement symbolic = add("a = 4.669");
		createSlider.execute(symbolic);
		GeoElement slider = lookup("a");
		removeSlider.execute(slider);

		symbolic = lookup("a");
		assertThat(symbolic, is(CoreMatchers.<GeoElement>instanceOf(GeoSymbolic.class)));
		assertThat(symbolic.getLabelSimple(), is("a"));
		assertThat(symbolic.isAlgebraLabelVisible(), is(true));
	}

	@Test
	public void isAvailable() {
		GeoElement numeric = add("a = 4.669");
		checkIsAvailableFor(numeric, "a");
		GeoElement angle = add("b = 4.669" + Unicode.alpha);
		checkIsAvailableFor(angle, "b");

		// Creates constant c_1
		add("Integral(x)");
		GeoElement constant = lookup("c_1");
		assertThat(removeSlider.isAvailable(constant), is(false));
	}

	private void checkIsAvailableFor(GeoElement element, String label) {
		createSlider.execute(element);
		GeoElement slider = lookup(label);
		assertThat(removeSlider.isAvailable(slider), is(true));
	}

}
