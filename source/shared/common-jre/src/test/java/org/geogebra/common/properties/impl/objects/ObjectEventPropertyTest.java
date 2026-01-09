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

import static org.geogebra.common.plugin.EventType.CLICK;
import static org.geogebra.common.plugin.EventType.DRAG_END;
import static org.geogebra.common.plugin.EventType.EDITOR_KEY_TYPED;
import static org.geogebra.common.plugin.EventType.LOAD_PAGE;
import static org.geogebra.common.plugin.EventType.UPDATE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EventType;
import org.junit.Before;
import org.junit.Test;

public class ObjectEventPropertyTest extends BaseUnitTest {

	private LocalizationJre loc;

	@Before
	public void setUp() throws Exception {
		loc = getApp().getLocalization();
	}

	@Test
	public void testGeoObjectEvents() {
		geoShouldHaveEvents(add("(1, 1)"), CLICK, UPDATE, DRAG_END, LOAD_PAGE);
		geoShouldHaveEvents(add("CheckBox[]"), UPDATE, LOAD_PAGE);
		geoShouldHaveEvents(add("InputBox[]"), CLICK, UPDATE, EDITOR_KEY_TYPED, LOAD_PAGE);
		GeoElement button = add("Button[]");
		button.setFixed(true);
		geoShouldHaveEvents(button, CLICK, UPDATE, LOAD_PAGE);
	}

	private void geoShouldHaveEvents(GeoElement geo, EventType... events) {
		for (EventType type: ElementObjectEventProperty.eventNames.keySet()) {
			ElementObjectEventProperty property = new ElementObjectEventProperty(loc, geo, type);
			assertEquals(List.of(events).contains(type), property.isEnabled());
		}
	}
}
