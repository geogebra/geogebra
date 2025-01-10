package org.geogebra.web.html5.util;

import elemental2.promise.Promise;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "navigator.permissions")
public class Permissions {

	public static native Promise<Permission> query(JsPropertyMap<Object> name);

	@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "PermissionStatus")
	public static class Permission {
		@JsProperty
		public String state;
	}
}
