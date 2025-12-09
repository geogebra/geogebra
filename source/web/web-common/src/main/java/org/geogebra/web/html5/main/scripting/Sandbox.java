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

package org.geogebra.web.html5.main.scripting;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.quickjs.emscripten.QuickJSResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.core.JsArray;
import elemental2.promise.Promise;
import jsinterop.base.Js;

public class Sandbox {

	private QuickJS.QuickJSContext context;
	private final Object exportedApi;
	private final SandboxConverter converter = new SandboxConverter();

	public Sandbox(Object exportedApi) {
		this.exportedApi = exportedApi;
	}

	/**
	 * Run script in a sandbox
	 * @param script script text
	 */
	public void run(String script) {
		if (context == null) {
			loadContext(exportedApi, converter).then(vm -> {
				context = Js.uncheckedCast(vm);
				context.unwrapResult(context.evalCode(script));
				return null;
			});
		} else {
			Object rawResult = context.unwrapResult(context.evalCode(script));
			Log.trace(rawResult);
		}
	}

	static Promise<Object> loadContext(Object exportedApi, SandboxConverter converter) {
		if (QuickJS.get() == null) {

			return new Promise<>((resolve, reject) -> {

				GWT.runAsync(QuickJS.class, new RunAsyncCallback() {
					@Override
					public void onFailure(Throwable throwable) {
						Log.error("Could not load sandbox");
					}

					@Override
					public void onSuccess() {
						JavaScriptInjector.inject(QuickJSResources.INSTANCE.qjs());
						resolve.onInvoke(QuickJS.afterLibraryLoaded(exportedApi, converter));
					}
				});
			});
		} else {
			return QuickJS.afterLibraryLoaded(exportedApi, converter);
		}
	}

	/**
	 * @param listener QuickJS handle
	 * @param args arguments (not sand-boxed)
	 * @return whether handle is valid in this context
	 */
	public boolean call(Object listener, Object[] args) {
		if (context != null && converter.isSandboxedFunction(listener)) {
			QuickJS.QuickJSHandle ref = converter.toSandboxObject(listener, context);
			callRef(ref, args);
			return true;
		}
		return false;
	}

	/**
	 * @param name global function name
	 * @param args arguments (not sand-boxed)
	 * @return whether global reference exists
	 */
	public boolean callByName(String name, Object[] args) {
		if (context != null) {
			QuickJS.QuickJSHandle ref = context.getProp(context.global, name);
			if (!"undefined".equals(context.typeof(ref))) {
				callRef(ref, args);
				return true;
			}
		}
		return false;
	}

	private void callRef(QuickJS.QuickJSHandle ref, Object[] args) {
		JsArray<Object> sandBoxedArgs = new JsArray<>();
		sandBoxedArgs.push(ref);
		sandBoxedArgs.push(context.global);
		for (Object arg : args) {
			sandBoxedArgs.push(converter.toSandboxObject(arg, context));
		}
		context.unwrapResult(context.callFunction.apply(context, sandBoxedArgs));
	}
}
