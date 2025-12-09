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

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class VerticalIncrementModelTest extends BaseUnitTest {

	@Test
	public void shouldChangeStep() {
		VerticalIncrementModel model = new VerticalIncrementModel(getApp());
		GeoPoint pt = add("A=(1,2)");
		model.setGeos(new GeoElement[]{pt});
		model.applyChanges("3+4");
		assertThat(pt.getVerticalIncrement(), hasValue("7"));
		reload();
		assertThat(((GeoPoint) lookup("A")).getVerticalIncrement(), hasValue("7"));
	}
}
