package org.geogebra.web.html5.main.scripting;

import org.geogebra.common.util.InjectJsInterop;

import elemental2.core.Function;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class QuickJS {

	@JsOverlay
	static QuickJS get() {
		return Js.uncheckedCast(Js.asPropertyMap(DomGlobal.window).get("QJS"));
	}

	native Promise<Object> getQuickJS();

	@JsType(isNative = true, namespace = JsPackage.GLOBAL)
	public static class QuickJSContext {

		@InjectJsInterop
		public QuickJSHandle global;
		public Function callFunction;

		@JsProperty
		public native QuickJSHandle getTrue();

		@JsProperty
		public native QuickJSHandle getFalse();

		@JsProperty
		public native QuickJSHandle getNull();

		@JsProperty
		public native QuickJSHandle getUndefined();

		public native Object unwrapResult(Object code);

		public native Object evalCode(String code);

		public native void setProp(QuickJSHandle target, String name, Object value);

		public native QuickJSHandle newArray();

		public native QuickJSHandle newObject();

		public native QuickJSHandle newFunction(String name, MethodWrapper nativeFn);

		public native QuickJSHandle newNumber(Object rawResult);

		public native QuickJSHandle newString(Object function);

		public native void dispose();

		public native String typeof(Object rawResult);

		public native Object dump(Object sandboxArg);

		public native QuickJSHandle getProp(QuickJSHandle global, String s);
	}

	@JsType(isNative = true, namespace = JsPackage.GLOBAL)
	static class ContextFactory {

		public native QuickJSContext newContext();
	}

	@JsType(isNative = true, namespace = JsPackage.GLOBAL)
	public static class QuickJSHandle {

		public native void dispose();
	}

	// this method would be a better fit for a non-native type, but it must be
	// in the same file as MethodWrapper class so that JsInterop with varargs works correctly
	@JsOverlay
	static Promise<Object> afterLibraryLoaded(Object exportedApi, SandboxConverter converter) {
		final JsPropertyMap<Object> bundle = Js.asPropertyMap(exportedApi);
		return QuickJS.get().getQuickJS().then(factory -> {
			QuickJSContext vm = Js.<QuickJS.ContextFactory>uncheckedCast(factory).newContext();
			QuickJSHandle ggbApplet = vm.newObject();
			QuickJSHandle window = vm.newObject();
			vm.setProp(vm.global, "window", window);
			vm.setProp(vm.global, "ggbApplet", ggbApplet);
			vm.setProp(window, "ggbApplet", ggbApplet);
			JsPropertyMap<?> methods = Js.asPropertyMap(exportedApi);
			methods.forEach(method -> {
				Function methodFn = (Function) bundle.get(method);
				MethodWrapper methodWrapper = (sandboxArgs) -> {
					JsArray<Object> realArgs = new JsArray<>();
					for (Object sandboxArg : sandboxArgs) {
						realArgs.push(converter.fromSandboxObject(sandboxArg, vm));
					}
					Object rawResult = methodFn.apply(bundle, realArgs);
					return converter.toSandboxObject(rawResult, vm);
				};
				vm.setProp(ggbApplet, method, vm.newFunction("", methodWrapper));
			});
			return Promise.resolve(vm);
		});
	}

	@JsFunction
	public interface MethodWrapper {
		Object call(Object... arguments);

	}
}
