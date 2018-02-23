package org.geogebra.web.main;

import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.DOM;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ ArticleElement.class, TextAreaElement.class })
public class UndoTest {

	@Test
	public void createUndo() {
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(new AppletFactory3D(), new GLookAndFeel(), new BrowserDevice(), false);
		fr.ae = ArticleElement.as(DOM.createElement("article"));
		fr.runAsyncAfterSplash();
		AppW app = (AppW) fr.getApplication();
		app.getKernel().storeUndoInfo();
	}
}
