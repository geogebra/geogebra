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

package org.geogebra.common.gui.dialog.options;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.dialog.options.model.VectorHeadStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAccumulator;
import org.junit.Before;
import org.junit.Test;

public class VectorHeadStyleTest extends BaseUnitTest {

	private VectorHeadStyleModel model;
	private GeoElement vector;

	@Before
	public void setupModel() {
		model = new VectorHeadStyleModel(getApp());
		vector = add("v=(1,1)");
		GeoElement[] geos = new GeoElement[]{vector};
		model.setGeos(geos);
	}

	@Test
	public void shouldUpdateProperties() {
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		model.apply(0, 1);
		assertEquals(Collections.singletonList("UPDATE_STYLE v"), listener.getEvents());
	}
}
