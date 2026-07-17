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

package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnableDynamicCaptionModelTest extends BaseUnitTest {

	private EnableDynamicCaptionModel model;
	private GeoElement point;

	@BeforeEach
	void setupModel() {
		model = new EnableDynamicCaptionModel(null, getApp());
		point = add("A=(1,1)");
		GeoElement[] geos = new GeoElement[]{point};
		model.setGeos(geos);
	}

	@Test
	void shouldUpdateProperties() {
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		model.apply(0, true);
		assertEquals(Collections.singletonList("UPDATE_STYLE A"), listener.getEvents());
	}

	@Test
	void checkShouldSetEmptyString() {
		model.apply(0, true);
		assertThat(point.getDynamicCaption(), notNullValue());
		assertThat(point.getDynamicCaption(), hasValue(""));
	}

	@Test
	void uncheckShouldSetNull() {
		model.apply(0, false);
		assertThat(point.getDynamicCaption(), nullValue());
	}
}
