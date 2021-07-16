package org.geogebra.web.full.gui.menubar.action;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.ListBox;

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
	 * Make sure asserts don't kill the tests
	 */
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	/**
	 * Undo / redo with a single slide.
	 */
	@Test
	public void fileNew() {
		app = AppMocker
				.mockApplet(new AppletParameters("notes")
						.setAttribute("vendor", "mebis"));
		BaseWidgetFactory factory = mock(BaseWidgetFactory.class);
		ListBox mockBox = mock(ListBox.class);
		when(factory.newListBox()).thenReturn(mockBox);
		app.getDialogManager().setWidgetFactory(factory);
		ClearAllAction action = new ClearAllAction(true);
		addObject("x");
		action.execute(null, app);
		app.getSaveController().cancel();
		Assert.assertEquals(0, app.getKernel().getConstruction()
				.getGeoSetConstructionOrder().size());
	}

	private static void addObject(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);

	}
}
