package org.geogebra.web.full.euclidian;

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
public class StyleBarTest {

	@Test
	public void updateGraphingStylebar() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("graphing"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

	private static void checkUpdate(EuclidianStyleBarW styleBar) {
		styleBar.setOpen(true);
		styleBar.updateStyleBar();
		styleBar.updateButtons();
		// mostly implicitly asserting that we didn't crash, but visibility can be checked too
		assertTrue("Style bar should be visible", styleBar.isVisible());
	}

	@Test
	public void updateWhiteboardStylebar() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("notes"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

}
