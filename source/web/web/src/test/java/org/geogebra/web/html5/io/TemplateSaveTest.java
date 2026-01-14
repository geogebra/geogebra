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

package org.geogebra.web.html5.io;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.gwtproject.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2D.class, RootPanel.class})
public class TemplateSaveTest {

	@Before
	public void initAssertions() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void testSaveTemplate() {
		AppletParameters articleElement = new AppletParameters("notes");
		AppWFull app = AppMocker.mockApplet(articleElement);
		app.getSaveController().setSaveType(Material.MaterialType.ggsTemplate);
		PenToolsSettings settings = app.getSettings().getPenTools();
		settings.setLastPenThickness(30);
		settings.setLastSelectedPenColor(GColor.newColor(204, 0, 153));
		settings.setLastHighlighterThickness(1);
		settings.setLastSelectedHighlighterColor(GColor.newColor(219, 97, 20));
		settings.setDeleteToolSize(61);
		String pathString = "src/test/resources/org/geogebra/web/html5/io/templateXML.txt";
		String fileContent = FileIO.load(pathString);
		XMLStringBuilder sb = new XMLStringBuilder();
		app.getActiveEuclidianView().getXML(sb, false);
        assertEquals(fileContent, sb.toString().trim());
	}
}
