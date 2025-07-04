package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class DrawDropdownListTest extends BaseUnitTest {

	private GGraphics2D graphics2D = new GGraphicsCommon();

	@Test
	public void dropdownShouldSelectFirstItem() {
		DrawDropDownList dropDownList = setupList("{1,2,3}");
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(graphics2D);
		assertEquals(3, dropDownList.getOptionCount());
	}

	@Test
	public void emptyStringShouldBeValidElement() {
		DrawDropDownList dropDownList = setupList("{\"a\", \"\", \"c\"}");
		assertEquals(0, dropDownList.getOptionCount());
		dropDownList.toggleOptions();
		dropDownList.draw(graphics2D);
		assertEquals(3, dropDownList.getOptionCount());
	}

	@Test
	public void spaceShouldCloseDropdown() {
		DrawDropDownList dl = setupList("{1,2,3}");
		dl.toggleOptions();
		dl.setHoverIndex(1);
		getApp().handleSpaceKey();
		assertFalse("Options should be hidden", dl.isOptionsVisible());
		assertEquals(0, ((GeoList) dl.getGeoElement()).getSelectedIndex());
	}

	@Test
	public void spaceShouldSelectItem() {
		DrawDropDownList dl = setupList("{1,2,3}");
		dl.toggleOptions();
		dl.setKeyboardSelectionIndex(1);
		getApp().handleSpaceKey();
		assertFalse("Options should be hidden", dl.isOptionsVisible());
		assertEquals(1, ((GeoList) dl.getGeoElement()).getSelectedIndex());
	}

	private DrawDropDownList setupList(String definition) {
		GeoList dropdown = add(definition);
		dropdown.setDrawAsComboBox(true);
		dropdown.setEuclidianVisible(true);
		dropdown.updateRepaint();
		DrawableND drawableFor = getDrawable(dropdown);
		assertNotNull(drawableFor);
		return (DrawDropDownList) drawableFor;
	}
}
