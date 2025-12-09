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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class ShowObjectPropertyTest extends BaseUnitTest {

	@Test
	public void testSetValue() {
		GeoNumeric slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		slider.setEuclidianVisible(false);

		ShowObjectProperty showObjectProperty = null;
		MinProperty minProperty = null;
		ElementColorProperty elementColorProperty = null;
		try {
			showObjectProperty = new ShowObjectProperty(getLocalization(), slider);
			minProperty =
					new MinProperty(getKernel().getAlgebraProcessor(), getLocalization(), slider);
			elementColorProperty = new ElementColorProperty(getLocalization(), slider);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}

		showObjectProperty.setValue(true);
		assertThat(showObjectProperty.isEnabled(), is(true));
		assertThat(minProperty.isEnabled(), is(true));
		assertThat(elementColorProperty.isEnabled(), is(true));

		showObjectProperty.setValue(false);
		assertThat(showObjectProperty.isEnabled(), is(true));
		assertThat(minProperty.isEnabled(), is(true));
		assertThat(elementColorProperty.isEnabled(), is(false));
	}
}