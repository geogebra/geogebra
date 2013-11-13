package geogebra.html5.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;


/**
 * @author gabor
 * 
 * To mimic async behaviour with a single script linker
 *
 */
public class RunAsync {
		
		/**
		 * instance to use
		 */
		public static RunAsync INSTANCE = GWT.create(RunAsync.class);
		
		/**
		 * @param callback RunasyncCallback implemented in superclass
		 */
		public void runAsyncCallback(final MyRunAsyncCallback callback) {
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					callback.onSuccess();
				}
				
				public void onFailure(Throwable reason) {
					callback.onFailure(reason);
				}
			});
		};
	
}
