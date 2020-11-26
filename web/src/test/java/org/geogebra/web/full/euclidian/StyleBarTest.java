package org.geogebra.web.full.euclidian;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.ComplexPanel;
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
		try {
			styleBar.setOpen(true);
			styleBar.updateStyleBar();
			styleBar.updateButtons();
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void updateWhiteboardStylebar() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("notes"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

}
