package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.EventType;
import org.geogebra.editor.share.util.Unicode;
import org.junit.Test;

public class GeoElementTest extends BaseUnitTest {

	@Test
	public void isSimple() {
		GeoElement minusOne = addAvInput("-1");
		assertThat(minusOne.isSimple(), is(true));

		GeoElement recurringDecimal = addAvInput("1.3" + Unicode.OVERLINE);
		assertThat(recurringDecimal.isSimple(), is(false));
	}

	@Test
	public void labelShouldStayInvisibleAfterRename() {
		GeoElement geo = add("a = 7");
		assertEquals("a = 7", geo.toString(StringTemplate.defaultTemplate));
		geo.rename(LabelManager.HIDDEN_PREFIX + "1");
		assertEquals("7", geo.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void definitionShouldHoldCorrectPrecision() {
		GeoElement geo = add("0.14579");
		getApp().setRounding("2");
		assertEquals("0.14579", geo.getDefinition(StringTemplate.maxPrecision));
	}

	@Test
	public void fixedByUserShouldBeProtected() {
		GeoElement geo = add("A1 = 7");
		geo.setFixed(true);
		assertTrue(geo.isProtected(EventType.UPDATE));
	}

	@Test
	public void fixedByDefaultShouldNotBeProtected() {
		List.of("A1:x=y", "A2:x").forEach(definition -> {
			GeoElement line = add(definition);
			assertTrue(line.isLocked());
			assertFalse(line.isProtected(EventType.UPDATE));
		});
	}
}