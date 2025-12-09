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

import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

final class SandboxConverter {

	private static final String FUNCTION_PREFIX = "ggbPersistentFn";

	private final JsArray<Object> listeners = new JsArray<>();

	Object fromSandboxObject(Object sandboxArg, QuickJS.QuickJSContext vm) {
		if ("function".equals(vm.typeof(sandboxArg))) {
			vm.setProp(vm.global, FUNCTION_PREFIX + listeners.length, sandboxArg);
			listeners.push(sandboxArg);
			return sandboxArg;
		}

		return vm.dump(sandboxArg);
	}

	QuickJS.QuickJSHandle toSandboxObject(Object rawResult, QuickJS.QuickJSContext vm) {
		return toSandboxObject(rawResult, vm, 5, rawResult);
	}

	private QuickJS.QuickJSHandle toSandboxObject(Object rawResult,
			QuickJS.QuickJSContext vm, int maxDepth, Object topLevel) {
		if (listeners.indexOf(rawResult) > -1) {
			return vm.getProp(vm.global, FUNCTION_PREFIX + listeners.indexOf(rawResult));
		}
		if (rawResult instanceof Integer) {
			return vm.newNumber(rawResult);
		}
		switch (Js.typeof(rawResult)) {
		case "undefined":
			return vm.getUndefined();
		case "boolean":
			return Js.isTruthy(rawResult) ? vm.getTrue() : vm.getFalse();
		case "number":
			return vm.newNumber(rawResult);
		case "string":
			return vm.newString(rawResult);
		case "object":
			if (Js.isFalsy(rawResult)) {
				return vm.getNull();
			}

			QuickJS.QuickJSHandle sandboxed = JsArray.isArray(rawResult)
					? vm.newArray() : vm.newObject();
			JsPropertyMap<Object> props = Js.asPropertyMap(rawResult);
			if (maxDepth > 0) {
				props.forEach(propName ->
					vm.setProp(sandboxed, propName,
							toSandboxObject(props.get(propName), vm, maxDepth - 1, topLevel))
				);
			} else {
				Log.warn("sandbox prop " + topLevel);
			}
			return sandboxed;
		default: // "bigint", "symbol", "function"
			Log.warn("Unexpected type: " + Js.typeof(rawResult));
			return vm.getUndefined();
		}
	}

	public boolean isSandboxedFunction(Object listener) {
		return listeners.includes(listener);
	}
}
