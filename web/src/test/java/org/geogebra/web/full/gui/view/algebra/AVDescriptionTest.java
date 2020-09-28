package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AVDescriptionTest {

	@Test
	public void geometryShouldUseLaTeXForFunctions() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("geometry"));
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("f:sqrt(x/2)", false);
		RadioTreeItem rte = new RadioTreeItem(app.getKernel().lookupLabel("f"));
		rte.doUpdate();
		Assert.assertTrue(rte.latex);
	}

	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}
}
