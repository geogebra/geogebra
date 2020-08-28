package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.junit.Assert;
import org.junit.Test;

public class ExamDrawerMenuFactoryTest {

	@Test
	public void testGraphingExam() {
		DrawerMenuFactory factory = new ExamDrawerMenuFactory(GeoGebraConstants.Version.GRAPHING);
		DrawerMenu menu = factory.createDrawerMenu();
		Assert.assertEquals(1, menu.getMenuItemGroups().size());
		MenuItemGroup group = menu.getMenuItemGroups().get(0);
		Assert.assertEquals(3, group.getMenuItems().size());
	}

	@Test
	public void testSwitchCalculator() {
		DrawerMenuFactory factory =
				new ExamDrawerMenuFactory(GeoGebraConstants.Version.SUITE, true);
		DrawerMenu menu = factory.createDrawerMenu();
		Assert.assertEquals(1, menu.getMenuItemGroups().size());
		MenuItemGroup group = menu.getMenuItemGroups().get(0);
		Assert.assertEquals(4, group.getMenuItems().size());
	}
}
