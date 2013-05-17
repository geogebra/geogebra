package geogebra.html5.util;


import com.google.gwt.dom.client.ScriptElement;

/**
 * @author gabor
 *
 * Helps to load JavaScript on demand
 */
public final class DynamicScriptElement extends ScriptElement {
	
	protected DynamicScriptElement() {	
	}
	
	/**
	 * @param handler
	 * if script loaded, calls the callback that implements interface ScriptLoadHandler
	 */
	public native void addLoadHandler(ScriptLoadCallback handler) /*-{
		this.addEventListener("load",function() {
			handler.@geogebra.html5.util.ScriptLoadCallback::onLoad()();
		},false);
	}-*/;
}
