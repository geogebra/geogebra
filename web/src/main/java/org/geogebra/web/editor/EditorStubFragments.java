package org.geogebra.web.editor;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;
import org.geogebra.common.util.Prover;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

public class EditorStubFragments {

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
		GWT.runAsync(CommandDispatcherStats.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
		GWT.runAsync(Prover.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
		GWT.runAsync(CommandDispatcherAdvanced.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
		GWT.runAsync(CommandDispatcherCAS.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
		GWT.runAsync(CommandDispatcherScripting.class, new RunAsyncCallback() {

			@Override
			public void onFailure(Throwable reason) {
				// never called
			}

			@Override
			public void onSuccess() {
				// never called
			}

		});
		GWT.runAsync(CommandDispatcherDiscrete.class, new RunAsyncCallback() {

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
