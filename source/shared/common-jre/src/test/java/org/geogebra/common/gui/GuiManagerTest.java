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

package org.geogebra.common.gui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.XMLStringBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GuiManagerTest extends BaseUnitTest {

	@Test
	void shouldSaveAlgebraViewSettingsWithoutPlatformView() {
		getSettings().getAlgebra().setTreeMode(AlgebraView.SortMode.ORDER);
		XMLStringBuilder builder = new XMLStringBuilder();
		GuiManager guiManager = Mockito.mock(GuiManager.class, Mockito.withSettings()
				.defaultAnswer(Mockito.CALLS_REAL_METHODS).useConstructor(getApp()));

		guiManager.getViewsXML(builder, false);

		assertThat(builder.toString(),
				containsString("<algebraView>\n\t<mode val=\"3\"/>\n</algebraView>"));
	}
}
