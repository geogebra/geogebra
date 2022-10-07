package org.geogebra.common.plugin;

public class JsReference {

	private final String text;
	private Object nativeRunnable;

	public JsReference(String string) {
		this.text = string;
	}

	public Object getNativeRunnable() {
		return nativeRunnable;
	}

	public String getText() {
		return text;
	}

	public void setNativeRunnable(Object nativeRunnable) {
		this.nativeRunnable = nativeRunnable;
	}
}
