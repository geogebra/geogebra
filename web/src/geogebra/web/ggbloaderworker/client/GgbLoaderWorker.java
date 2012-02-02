package geogebra.web.ggbloaderworker.client;

import com.google.gwt.webworker.client.DedicatedWorkerEntryPoint;
import com.google.gwt.webworker.client.MessageEvent;
import com.google.gwt.webworker.client.MessageHandler;

public class GgbLoaderWorker extends DedicatedWorkerEntryPoint {

	@Override
	public void onWorkerLoad() {
		
		this.setOnMessage(new MessageHandler() {
			
			public void onMessage(MessageEvent event) {
				postMessage("worker msg"+event.getDataAsString());
			}
		});
	}

}
