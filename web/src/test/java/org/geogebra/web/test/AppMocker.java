package org.geogebra.web.test;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.impl.PopupImpl;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.fakes.FakeProvider;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class AppMocker {

	public static AppWFull mockGraphing(Class<?> testClass) {
		return mockApp("graphing", testClass);
	}

	public static AppWFull mockCas(Class<?> testClass) {
		return mockApp("cas", testClass);
	}

	private static AppWFull mockApp(String appName, Class<?> testClass) {
		testClass.getClassLoader().setDefaultAssertionStatus(false);
		return mockApplet(new TestArticleElement("prerelease", appName));
	}

	public static AppWFull mockApplet(ArticleElementInterface ae) {
		GwtMockito.useProviderForType(PopupImpl.class,
				new FakeProvider<PopupImpl>() {

					@Override
					public PopupImpl getFake(Class<?> type) {
						return new PopupImpl();
					}
				});
		GwtMockito.useProviderForType(ClientBundle.class, new CustomFakeClientBundleProvider());
		Browser.mockWebGL();
		FactoryProvider.setInstance(new MockFactoryProviderGWT());
		GeoGebraFrameFull fr = new GeoGebraFrameFull(new AppletFactory3D() {
			@Override
			public AppWFull getApplet(ArticleElementInterface params,
									  GeoGebraFrameFull frame, GLookAndFeelI laf, GDevice device) {
				return new AppWapplet3DTest(params, frame, (GLookAndFeel) laf, device);
			}
		},
				new GLookAndFeel(), new BrowserDevice(), ae);
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
		fr.runAsyncAfterSplash();
		AppWFull app = fr.getApp();
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();
		return app;
	}
}
