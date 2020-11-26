package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.WithClassesToStub;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;


@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2d.class, RootPanel.class})
public class TemplateSaveTest {
	private static AppWFull app;

	@Test
	public void testSaveTemplate() {
		AppletParameters articleElement = new AppletParameters("notes");
		app = AppMocker.mockApplet(articleElement);
		app.getSaveController().setSaveType(Material.MaterialType.ggsTemplate);
		EuclidianSettings settings = app.getActiveEuclidianView().getSettings();
		settings.setLastPenThickness(30);
		settings.setLastSelectedPenColor(GColor.newColor(204,0,153));
		settings.setLastHighlighterThinckness(1);
		settings.setLastSelectedHighlighterColor(GColor.newColor(219,97,20));
		settings.setDeleteToolSize(61);
		String pathString = "src/test/java/org/geogebra/web/html5/main/templateXML.txt";
		String fileContent = FileIO.load(pathString);
		StringBuilder sb = new StringBuilder();
		app.getActiveEuclidianView().getXML(sb, false);
        Assert.assertEquals(sb.toString().trim(), fileContent);
	}
}
