package geogebra.web.ggbloaderworker.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.webworker.client.DedicatedWorkerEntryPoint;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;

public class GgbLoaderWorker extends DedicatedWorkerEntryPoint {

	@Override
	public void onWorkerLoad() {
		
		importScript("../web/js/jsxgraph_unzip.js");
		
		this.setOnMessage(new MessageHandler() {
			
			public void onMessage(MessageEvent event) {
				postMessage(stringify(unzip(event.getDataAsJSO())));
			}
		});
	}

	protected native String stringify(JsArray<JsArrayString> unzip) /*-{
	    return JSON.stringify(unzip);
    }-*/;

	protected native JsArray<JsArrayString>  unzip(JavaScriptObject data) /*-{
	   if (Array.isArray(data)) {
	   		return (new ggm.JXG.Util.Unzip(data)).unzip();
	   } else {
	   		var arr = [];
	   		for (var i = 0, l = data.length; i < l; i++) {
	   			arr.push(data[i]);
	   		}
	   		return (new ggm.JXG.Util.Unzip(arr)).unzip();
	   }
    }-*/;

}
