package org.geogebra.web.full.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.FocusUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(FocusUtil.class)
public class ThumbnailTest {

	@Test
	public void thumbnailShouldUseNonemptyView() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("classic"));

		thumbnailShouldUse(App.VIEW_EUCLIDIAN, app);

		app.getGgbApi().setPerspective("GT");
		thumbnailShouldUse(App.VIEW_EUCLIDIAN3D, app);

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("SetActiveView(1)", true);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("\"Text\"",
				true);
		thumbnailShouldUse(App.VIEW_EUCLIDIAN, app);
	}

	private static void thumbnailShouldUse(int viewId, AppW app) {
		assertEquals(viewId,
				app.getGgbApi().getViewForThumbnail().getViewID());
	}

}
