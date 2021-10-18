package org.geogebra.web.simple;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Replacement for 3D split point
 */
public class Stub3DFragment {

	/**
	 * This tricks GWT compiler into thinking that CommandDispatcher3D is a
	 * valid split point. The method needs to be called, otherwise it's removed
	 * by optimizer and the split point is not valid.
	 */
	public static void load() {
		// we don't want to wait for the actual fragment to load
		// simple if(false) might cause problems with optimization
		if (System.currentTimeMillis() > 1) {
			return;
		}
		GWT.runAsync(CommandDispatcher3D.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
	}
}
