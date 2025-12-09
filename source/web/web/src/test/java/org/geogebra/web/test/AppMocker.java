/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.geogebra.common.main.App;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.cas.giac.CASFactoryW;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.BrowserDevice;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.AppletFactory3D;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.impl.PopupImpl;

import com.google.gwtmockito.GwtMockito;
import com.himamis.retex.renderer.web.FactoryProviderGWT;

import jsinterop.base.JsPropertyMap;

public class AppMocker {

	private static class TestLog extends Log {

		@Override
		public void print(Level level, Object logEntry) {
			if (logEntry instanceof Throwable) {
				((Throwable) logEntry).printStackTrace(System.out);
			} else {
				System.out.println(logEntry);
			}
		}

	}

	/**
	 * @return mock of graphing app
	 */
	public static AppWFull mockGraphing() {
		return mockApp("graphing");
	}

	/**
	 * @return mock of cas app
	 */
	public static AppWFull mockCas() {
		return mockApp("cas");
	}

	/**
	 * @return mock of geometry app
	 */
	public static AppWFull mockGeometry() {
		return mockApp("geometry");
	}

	/**
	 * @return mock of notes app
	 */
	public static AppWFull mockNotes() {
		return mockApp("notes");
	}

	/**
	 * @param appName app name
	 * @return mock app
	 */
	private static AppWFull mockApp(String appName) {
		return mockApplet(new AppletParameters(appName));
	}

	/**
	 * @return mock of scientific app
	 */
	public static AppW mockScientific() {
		return mockApp("scientific");
	}

	/**
	 * @param ae applet parameters
	 * @return mock applet
	 */
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
		Log.setLogger(new TestLog());
	}

	private static void setAppDefaults(App app) {
		app.setUndoRedoMode(UndoRedoMode.GUI);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();
	}

	/**
	 * @param ae applet parameters
	 * @return mock applet
	 */
	public static AppWsimple mockAppletSimple(AppletParameters ae) {
		useCommonFakeProviders();
		GeoGebraFrameSimple frame = new GeoGebraFrameSimple(DomMocker.getGeoGebraElement(), ae,
				new CASFactoryW());
		AppWsimple app = new AppWSimpleMock(ae, frame, false);
		setAppDefaults(app);
		return app;
	}

	/**
	 * Mock localization.
	 * @param translation maps key (ignoring category) to value
	 */
	public static void mockLocalization(Function<String, String> translation) {
		GeoGebraGlobal.__GGB__keysVar = mock(JsPropertyMap.class);
		JsPropertyMap<JsPropertyMap<String>> bundle = mock(JsPropertyMap.class);
		when(GeoGebraGlobal.__GGB__keysVar.get(any())).thenReturn(bundle);
		JsPropertyMap<String> category = mock(JsPropertyMap.class);
		when(bundle.get(any())).thenReturn(category);
		when(category.get(any())).thenAnswer(args ->
				translation.apply(args.getArgumentAt(0, String.class)));
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
		FactoryProviderGWT.ensureLoaded();
		setTestLogger();
	}

}
