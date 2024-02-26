package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for Undo with multiple slides
 *
 * @author Zbynek
 *
 */
@RunWith(GgbMockitoTestRunner.class)
public class ClearAllActionTest {

	private static AppWFull app;

	/**
	 * Undo / redo with a single slide.
	 */
	@Test
	public void fileNew() {
		app = AppMocker
				.mockApplet(new AppletParameters("notes")
						.setAttribute("vendor", "mebis"));
		ClearAllAction action = new ClearAllAction(true);
		addObject("x");
		action.execute(app);
		app.getSaveController().cancel();
		Assert.assertEquals(0, app.getKernel().getConstruction()
				.getGeoSetConstructionOrder().size());
	}

	private static void addObject(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);

	}
}
