package org.geogebra.web.test;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

import com.google.gwt.core.client.impl.SchedulerImpl;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.impl.PopupImpl;
import com.google.gwtmockito.GwtMockito;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

public class AppMocker {

	private static class MyLog extends Log {

		@Override
		public void print(Level level, Object logEntry) {
			System.out.println(logEntry);
		}
	}

	public static AppWFull mockGraphing(Class<?> testClass) {
		return mockApp("graphing", testClass);
	}

	public static AppWFull mockCas(Class<?> testClass) {
		return mockApp("cas", testClass);
	}

	public static AppWFull mockGeometry(Class<?> testClass) {
		return mockApp("geometry", testClass);
	}

	public static AppWFull mockNotes(Class<?> testClass) {
		return mockApp("notes", testClass);
	}

	private static AppWFull mockApp(String appName, Class<?> testClass) {
		testClass.getClassLoader().setDefaultAssertionStatus(false);
		return mockApplet(new AppletParameters(appName));
	}

	public static AppWFull mockApplet(AppletParameters ae) {
		useCommonFakeProviders();
		AppletFactory factory = new AppletFactory3D() {
			@Override
			public AppWFull getApplet(GeoGebraElement element,
					AppletParameters params,
					GeoGebraFrameFull frame, GLookAndFeelI laf, GDevice device) {
				return new AppWapplet3DTest(params, frame, (GLookAndFeel) laf, device);
			}
		};
		GeoGebraFrameFull fr = new GeoGebraFrameFull(factory,
				new GLookAndFeel(), new BrowserDevice(), DomMocker.getGeoGebraElement(), ae);
		fr.runAsyncAfterSplash();
		AppWFull app = fr.getApp();
		setAppDefaults(app);
		return app;
	}

	private static void setTestLogger() {
		Log.setLogger(new MyLog());
	}

	private static void setAppDefaults(App app) {
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();
	}

	public static AppWsimple mockAppletSimple(AppletParameters ae) {
		useCommonFakeProviders();
		useProviderForSchedulerImpl();
		GeoGebraFrameSimple frame = new GeoGebraFrameSimple(DomMocker.getGeoGebraElement(), ae);
		AppWsimple app = new AppWSimpleMock(ae, frame, false);
		setAppDefaults(app);
		return app;
	}

	private static void useCommonFakeProviders() {
		ElementalMocker.setupElemental();
		GwtMockito.useProviderForType(PopupImpl.class,
				type -> new PopupImpl() {

					@Override
					public Element getStyleElement(Element popup) {
						return DomMocker.getElement();
					}
				});
		Browser.mockWebGL();
		FactoryProvider.setInstance(new FactoryProviderGWT());
		setTestLogger();
	}

	public static void useProviderForSchedulerImpl() {
		GwtMockito.useProviderForType(SchedulerImpl.class,
				type -> new QueueScheduler());
	}
}
