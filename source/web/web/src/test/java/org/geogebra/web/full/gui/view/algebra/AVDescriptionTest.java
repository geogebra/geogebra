package org.geogebra.web.full.gui.view.algebra;

import static org.junit.Assert.assertTrue;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({ComplexPanel.class})
public class AVDescriptionTest {

	@Test
	public void geometryShouldUseLaTeXForFunctions() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("geometry"));
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("f:sqrt(x/2)", false);
		RadioTreeItem rte = new RadioTreeItem(app.getKernel().lookupLabel("f"));
		rte.doUpdate();
		assertTrue(rte.latex);
	}

}
