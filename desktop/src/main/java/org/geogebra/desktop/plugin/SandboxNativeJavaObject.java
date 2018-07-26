package org.geogebra.desktop.plugin;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * https://codeutopia.net/blog/2009/01/02/sandboxing-rhino-in-java/
 *
 */
public class SandboxNativeJavaObject extends NativeJavaObject {
	public SandboxNativeJavaObject(Scriptable scope, Object javaObject,
			Class staticType) {
		super(scope, javaObject, staticType);
	}

	@Override
	public Object get(String name, Scriptable start) {
		if (name.equals("getClass")) {
			return NOT_FOUND;
		}

		return super.get(name, start);
	}
}