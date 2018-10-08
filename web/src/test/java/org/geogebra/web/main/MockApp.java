package org.geogebra.web.main;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.TestArticleElement;

import com.google.gwt.user.client.ui.impl.PopupImpl;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.fakes.FakeProvider;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class MockApp {
	public static AppWFull mockApplet(TestArticleElement ae) {
		GwtMockito.useProviderForType(PopupImpl.class,
				new FakeProvider<PopupImpl>() {

					@Override
					public PopupImpl getFake(Class<?> type) {
						return new PopupImpl();
					}
				});
		Browser.mockWebGL();
		FactoryProvider.setInstance(new MockFactoryProviderGWT());
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(new AppletFactory3D(),
				new GLookAndFeel(), new BrowserDevice(), false);
		Log.setLogger(new Log() {

			@Override
			protected void print(String logEntry, Level level) {
				System.out.println(logEntry);
			}

			@Override
			public void doPrintStacktrace(String message) {
				new Throwable(message).printStackTrace();

			}
		});
		fr.ae = ae;
		fr.runAsyncAfterSplash();
		AppWFull app = (AppWFull) fr.getApplication();
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();
		return app;
	}
}
