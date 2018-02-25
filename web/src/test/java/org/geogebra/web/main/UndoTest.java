package org.geogebra.web.main;

import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListController;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.ui.impl.PopupImpl;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.gwtmockito.fakes.FakeProvider;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.parser.NodeW;
import com.himamis.retex.renderer.web.resources.xml.XmlResources;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class, NodeW.class })
public class UndoTest {
	private static AppWFull app;

	@Test
	public void createUndo() {
		initMocks();
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("x", true);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("-x", true);
		shouldHaveUndoPoints(2);

		app.getGgbApi().undo();
		shouldHaveUndoPoints(1);

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		shouldHaveUndoPoints(2);
		shouldHaveSlides(2);
		slideShouldHaveObjects(0, 1);
		slideShouldHaveObjects(1, 0);

		app.getPageController().reorder(0, 1);
		app.getAppletFrame().getPageControlPanel().update();
		slideShouldHaveObjects(0, 0);
		slideShouldHaveObjects(1, 1);

		app.getGgbApi().undo();
		slideShouldHaveObjects(0, 1);
		slideShouldHaveObjects(1, 0);


		app.getAppletFrame().getPageControlPanel().duplicatePage(
				((PageListController) app.getPageController()).getCard(0));
		shouldHaveSlides(3);
		slideShouldHaveObjects(0, 1);
		slideShouldHaveObjects(1, 1);
		slideShouldHaveObjects(2, 0);

		app.getGgbApi().undo();
		shouldHaveSlides(2);
		slideShouldHaveObjects(0, 1);
		slideShouldHaveObjects(1, 0);
	}

	private void slideShouldHaveObjects(int slide, int expectedCount) {
		String xml = app.getPageController().getSlide(slide)
				.get("geogebra.xml");
		int start = 0;
		int count = 0;
		while (xml.indexOf("<element", start) > 0) {
			count++;
			start = xml.indexOf("<element", start) + 1;
		}
		Assert.assertEquals(expectedCount, count);
	}

	private static void initMocks() {
		GwtMockito.useProviderForType(XmlResources.class,
				new TextResourceProvider());
		GwtMockito.useProviderForType(PopupImpl.class,
				new FakeProvider<PopupImpl>() {

					public PopupImpl getFake(Class<?> type) {
						return new PopupImpl();
					}
				});
		FactoryProvider.setInstance(new MockFactoryProviderGWT());
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(new AppletFactory3D(),
				new GLookAndFeel(), new BrowserDevice(), false);
		fr.ae = new TestArticleElement();
		fr.runAsyncAfterSplash();
		app = (AppWFull) fr.getApplication();
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();

	}

	private static void shouldHaveSlides(int expected) {
		Assert.assertEquals(expected, app.getPageController().getSlideCount());

	}

	private static void shouldHaveUndoPoints(int expected) {
		Assert.assertEquals(expected, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		
	}
}
