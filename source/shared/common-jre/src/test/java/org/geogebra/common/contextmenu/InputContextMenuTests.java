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

package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.MathTemplates;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class InputContextMenuTests extends BaseUnitTest {

	@Test
	void testWithHelpDisabled() {
		assertEquals(
				List.of(Expression, Text), ContextMenuFactory.makeInputContextMenu(false, Set.of()));
	}

	@Test
	void testWithHelpEnabled() {
		assertEquals(
				List.of(Expression, Text, Help), ContextMenuFactory.makeInputContextMenu(true, Set.of()));
	}

	@Test
	@Issue("APPS-8121")
	void testWithMathTemplates() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		assertEquals(
				List.of(Expression, MathTemplates, Text, Help),
				ContextMenuFactory.makeInputContextMenu(true, Set.of()));
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	@Issue("APPS-8121")
	void testMathTemplatesSubmenu() {
		assertEquals(
				List.of(
						MathTemplateContextMenuItem.Vector,
						MathTemplateContextMenuItem.Matrix,
						MathTemplateContextMenuItem.Integral),
				ContextMenuFactory.makeMathTemplatesContextMenu(Set.of()));
	}
}
