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

package org.geogebra.common.restrictions;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.gui.view.algebra.filter.DefaultAlgebraOutputFilter;
import org.geogebra.common.gui.view.algebra.filter.ProtectiveAlgebraOutputFilter;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.jupiter.api.Test;

public class RestrictionsControllerTests extends BaseExamTestSetup {

	@Test
	public void testRestrictedSubApp() {
		setupApp(SuiteSubApp.GRAPHING);
		Restrictions mmsRestrictions = ExamType.MMS.createRestrictions();
		restrictionsController.applyRestrictions(mmsRestrictions);
		assertTrue(restrictionsController.getDisabledSubAppCodes()
				.contains(SuiteSubApp.GRAPHING.appCode));
		assertEquals(SuiteSubApp.CAS, getCurrentSubApp());
	}

	@Test
	public void testRestrictions() {
		setupApp(SuiteSubApp.GRAPHING);
		restrictionsController.applyRestrictions(ExamType.CVTE.createRestrictions());

		assertAll(
				// feature restrictions
				() -> assertTrue(restrictionsController
						.isFeatureRestricted(FeatureRestriction.HIDE_CALCULATED_EQUATION)),
				// command restrictions
				() -> assertFalse(getCommandDispatcher()
						.isAllowedByCommandFilters(Commands.Difference)),
				// expression restrictions
				() -> assertNull(evaluate("{{1,2},{3,4}}")),
				// context menu restrictions
				() -> assertEquals(
						List.of(Expression, Text, Help),
						ContextMenuFactory.makeInputContextMenu(true,
								restrictionsController.getContextMenuItemFilters())));

		restrictionsController.removeRestrictions();
		assertTrue(getCommandDispatcher().isAllowedByCommandFilters(Commands.Derivative));
		assertFalse(restrictionsController.isFeatureRestricted(
				FeatureRestriction.HIDE_SPECIAL_POINTS));
	}

	@Test
	public void testAppAlgebraOutputFilterUsesRestrictionsController() {
		setupApp(SuiteSubApp.GRAPHING);
		AlgebraOutputFilter base = getApp().getAlgebraOutputFilter();

		restrictionsController.applyRestrictions(ExamType.CVTE.createRestrictions());
		assertNotSame(base, getApp().getAlgebraOutputFilter());
		assertNull(evaluate("{{1,2},{3,4}}"));

		restrictionsController.removeRestrictions();
		restrictionsController.resetRestrictions();
		assertInstanceOf(DefaultAlgebraOutputFilter.class, getApp().getAlgebraOutputFilter());
		assertNotNull(evaluate("{{1,2},{3,4}}"));
	}
}
