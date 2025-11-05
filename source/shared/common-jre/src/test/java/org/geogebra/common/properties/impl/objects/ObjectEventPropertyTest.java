package org.geogebra.common.properties.impl.objects;

import static org.geogebra.common.plugin.EventType.CLICK;
import static org.geogebra.common.plugin.EventType.DRAG_END;
import static org.geogebra.common.plugin.EventType.EDITOR_KEY_TYPED;
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
		geoShouldHaveEvents(add("(1, 1)"), CLICK, UPDATE, DRAG_END);
		geoShouldHaveEvents(add("CheckBox[]"), UPDATE);
		geoShouldHaveEvents(add("InputBox[]"), CLICK, UPDATE, EDITOR_KEY_TYPED);
		GeoElement button = add("Button[]");
		button.setFixed(true);
		geoShouldHaveEvents(button, CLICK, UPDATE);
	}

	private void geoShouldHaveEvents(GeoElement geo, EventType... events) {
		for (EventType type: ElementObjectEventProperty.eventNames.keySet()) {
			ElementObjectEventProperty property = new ElementObjectEventProperty(loc, geo, type);
			assertEquals(List.of(events).contains(type), property.isEnabled());
		}
	}
}
