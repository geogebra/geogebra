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

package org.geogebra.common.gui.menu.impl;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.Before;
import org.junit.Test;

public class ExamDrawerMenuFactoryTest extends BaseAppTestSetup {

	@Test
	public void testGraphingExam() {
		setupGraphingApp();
		DrawerMenuFactory factory = new ExamDrawerMenuFactory(GeoGebraConstants.Version.GRAPHING);
		DrawerMenu menu = factory.createDrawerMenu(getApp());
		assertEquals(1, menu.getMenuItemGroups().size());
		MenuItemGroup group = menu.getMenuItemGroups().get(0);
		assertEquals(5, group.getMenuItems().size());
	}

	@Test
	public void testSwitchCalculator() {
		setupApp(SuiteSubApp.GRAPHING);
		DrawerMenuFactory factory =
				new ExamDrawerMenuFactory(GeoGebraConstants.Version.SUITE, true);
		DrawerMenu menu = factory.createDrawerMenu(getApp());
		assertEquals(1, menu.getMenuItemGroups().size());
		MenuItemGroup group = menu.getMenuItemGroups().get(0);
		assertEquals(6, group.getMenuItems().size());
	}

	@Test
	public void testScientificSuiteExam() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		DrawerMenuFactory factory = new ExamDrawerMenuFactory(GeoGebraConstants.Version.SCIENTIFIC,
				true);
		DrawerMenu menu = factory.createDrawerMenu(getApp());
		assertEquals(1, menu.getMenuItemGroups().size());
		MenuItemGroup group = menu.getMenuItemGroups().get(0);
		assertEquals(6, group.getMenuItems().size());
	}
}
