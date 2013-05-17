package geogebra.web.helper;

import geogebra.html5.util.ScriptLoadCallback;

import com.google.gwt.dom.client.IFrameElement;

/**
 * @author gabor
 * 
 * Loads an external script into an iFrame Element,
 * and renames it into the current nameSpace.
 *
 */
public class ScriptNameSpace extends IFrameElement {
	
	protected ScriptNameSpace() {
	}
	
	/**
	 * @param scriptUrl the script to load
	 * @param fromNameSpace  the namespace we like to rename from
	 * @param toNameSpace	the namespace we like to rename to
	 * @param callback	will be called when process finished
	 * 
	 * renames the original namespace in the script to the given namespace
	 */
	public final native void renameNameSpace(String scriptUrl,String fromNameSpace, String toNameSpace, ScriptLoadCallback callback) /*-{
		var innerDocument,
			innerWindow,
			bodyElement;
			this.style.display = "none";
			this.onload = function() {
				innerWindow = this.contentWindow;
				innerDocument = innerWindow.document;
				script = innerDocument.createElement('script');
				script.src = scriptUrl;
				script.addEventListener("load", function() {
					$wnd[toNameSpace] = innerWindow[fromNameSpace];
					$wnd.console.log($wnd[toNameSpace]);
					callback.@geogebra.html5.util.ScriptLoadCallback::onLoad()();
				});
			innerDocument.body.appendChild(script);
		};
		this.srcdoc = '<html><head></head><body></body></html>';
		
	}-*/;
}
