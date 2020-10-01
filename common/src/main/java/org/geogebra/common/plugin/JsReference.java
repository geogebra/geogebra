package org.geogebra.common.plugin;

import java.util.HashMap;

public class JsReference {

	private static HashMap<String, JsReference> nameToScript = new HashMap<>();
	private final String text;
	private Object nativeRunnable;

	public JsReference(String string) {
		this.text = string;
	}

	/**
	 * @param string script name
	 * @return script
	 */
	public static JsReference fromName(String string) {
		if (nameToScript == null) {
			nameToScript = new HashMap<>();
		} else if (nameToScript.containsKey(string)) {
			return nameToScript.get(string);
		}
		JsReference script = new JsReference(string);
		nameToScript.put(string, script);
		return script;
	}

	/**
	 * @param nativeRunnable native representation of JS function
	 * @return JS function wrapped as reference
	 */
	public static JsReference fromNative(Object nativeRunnable) {
		if (nativeRunnable instanceof String) {
			return fromName((String) nativeRunnable);
		}
		for (JsReference ref : nameToScript.values()) {
			if (ref.getNativeRunnable() == nativeRunnable) {
				return ref;
			}
		}
		JsReference alias = fromName((nameToScript.size() + 1) + "");
		alias.nativeRunnable = nativeRunnable;
		return alias;
	}

	public Object getNativeRunnable() {
		return nativeRunnable;
	}

	public String getText() {
		return text;
	}
}
