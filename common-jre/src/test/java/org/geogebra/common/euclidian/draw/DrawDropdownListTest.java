package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class DrawDropdownListTest extends BaseUnitTest {

	@Test
	public void dropdownShouldSelectFirstItem() {
		GeoList dropdown = add("{1,2,3}");
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getDrawable(dropdown);
		assertNotNull(drawableFor);
		DrawDropDownList dropDownList = (DrawDropDownList) drawableFor;
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(getApp().getActiveEuclidianView().getGraphicsForPen());
		assertEquals(3, dropDownList.getOptionCount());
	}

	@Test
	public void emptyStringShouldBeValidElement() {
		GeoList dropdown = add("{\"a\", \"\", \"c\"}");
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getDrawable(dropdown);
		assertNotNull(drawableFor);
		DrawDropDownList dropDownList = (DrawDropDownList) drawableFor;
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(getApp().getActiveEuclidianView().getGraphicsForPen());
		assertEquals(3, dropDownList.getOptionCount());
	}
}
