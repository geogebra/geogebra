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

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NameCaptionPropertyTest extends BaseUnitTest {

	@Test
	void testChangePointName() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assertions.assertEquals("A", property.getValue());
		Assertions.assertEquals(LABEL_NAME, point.getLabelMode());
		point.setLabel("B");
		Assertions.assertEquals("B", property.getValue());
		Assertions.assertEquals(LABEL_NAME, point.getLabelMode());
	}

	@Test
	void testInvalidNameBecomesCaption() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
			NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assertions.assertEquals("A", property.getValue());
		property.doSetValue("point caption");
		Assertions.assertEquals("A", point.getLabelSimple());
		Assertions.assertEquals("point caption", point.getCaptionSimple());
		Assertions.assertEquals(LABEL_CAPTION, point.getLabelMode());
	}

	@Test
	void testNameToCaptionAndBack() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		NameCaptionProperty property = new NameCaptionProperty(getLocalization(), point);
		Assertions.assertEquals("A", property.getValue());
		property.doSetValue("point caption");
		property.doSetValue("A");
		Assertions.assertEquals("A", point.getLabelSimple());
		Assertions.assertEquals(LABEL_NAME, point.getLabelMode());
	}
}
