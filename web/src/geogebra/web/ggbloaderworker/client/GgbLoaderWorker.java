package geogebra.web.ggbloaderworker.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.webworker.client.DedicatedWorkerEntryPoint;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;

public class GgbLoaderWorker extends DedicatedWorkerEntryPoint {

	@Override
	public void onWorkerLoad() {
		
		//importScript("../web/js/jsxgraph_unzip.js");
		
		this.setOnMessage(new MessageHandler() {
			
			public void onMessage(MessageEvent event) {
				postMessage("worker msg"+event.getDataAsString()); 
				//postMessage(stringify(unzip(event.getDataAsString())));
			}
		});
	}

	protected native String stringify(JsArray<JsArrayString> unzip) /*-{
	    return JSON.stringify(unzip);
    }-*/;

	protected native JsArray<JsArrayString>  unzip(String data) /*-{
	   var data_array = JSON.parse(data);
	   return (new ggm.JXG.Util.Unzip(data_array)).unzip();
    }-*/;

}
