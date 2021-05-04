package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class DrawDropdownListTest extends BaseUnitTest {

	@Test
	public void dropdownShouldSelectFirstItem() {
		GeoList dropdown = add("{1,2,3}");
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getApp().getActiveEuclidianView().getDrawableFor(dropdown);
		assertNotNull(drawableFor);
		assertEquals(0, ((DrawDropDownList) drawableFor).getOptionCount());
		((DrawDropDownList) drawableFor).setHoverIndex(0);
		assertEquals(3, ((DrawDropDownList) drawableFor).getOptionCount());
	}
}
